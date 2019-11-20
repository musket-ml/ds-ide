package com.onpositive.dside.tasks.analize;

import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SashElement;
import com.onpositive.commons.elements.ToolbarElement;
import com.onpositive.dside.dto.DataSetAnalisysRequest;
import com.onpositive.dside.dto.DataSetFilter;
import com.onpositive.dside.dto.GetPossibleAnalisisResult;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.ui.DataSetGallery;
import com.onpositive.dside.ui.DynamicUI;
import com.onpositive.dside.ui.ModelEvaluationSpec;
import com.onpositive.dside.ui.VirtualTable;
import com.onpositive.dside.ui.VisualizerViewer;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentLogs;
import com.onpositive.musket_core.IDataSet;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;
import com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.semantic.ui.core.Alignment;
import com.onpositive.semantic.ui.core.Rectangle;
import com.onpositive.semantic.ui.workbench.elements.XMLView;
import com.onpositive.yamledit.introspection.InstrospectedFeature;
import com.onpositive.yamledit.introspection.IntrospectedParameter;
import com.onpositive.yamledit.io.YamlIO;

public class AnalistsView extends XMLView {

	private ModelEvaluationSpec model;
	private String dataset;
	private Experiment experiment;
	private GetPossibleAnalisisResult options;
	private boolean inited;
	private ComboEnumeratedValueSelector<Object> visualizers;
	private ComboEnumeratedValueSelector<Object> analizers;
	private GateWayRelatedTask task;
	private boolean isData;
	private HashMap<String, Object> visualizationParams = new HashMap<>();
	private HashMap<String, Object> analisisParams = new HashMap<>();
	private Image image;
	private InstrospectedFeature visualizerFeature;
	private ComboEnumeratedValueSelector<String> stageSelector;

	protected ArrayList<DataSetFilter> filters = new ArrayList<>();
	private ArrayList<String> datasetStages;
	private ArrayList<InstrospectedFeature> filterKinds = new ArrayList<>();

	private ArrayList<CompositeEditor> filterUIs = new ArrayList<>();
	private JFreeChart createChart;
	private Container element2;

	public AnalistsView() {
		super("dlf/analisysView.dlf");
	}

	public void addFilter() {
		DataSetFilter object = new DataSetFilter();
		object.setMode("normal");
		object.getModes().add("normal");
		object.getModes().add("inverse");
		object.getKinds().addAll(filterKinds.stream().map(x -> x.getName()).collect(Collectors.toList()));
		object.getStages().addAll(datasetStages);
		object.setApplyAt(datasetStages.get(datasetStages.size() - 1));
		IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(object, null, null);
		filters.add(object);
		Binding binding = new Binding(object);
		AbstractUIElement<?> createWidget = (AbstractUIElement<?>) widgetObject.createWidget(binding);
		Container c = (Container) getElement("filters_section");
		c.add(createWidget);
		c.getControl().layout(true, true);
		filterUIs.add((CompositeEditor) createWidget);
	}

	public void removeFilter() {
		Container c = (Container) getElement("filters_section");
		if (!c.getChildren().isEmpty()) {

			CompositeEditor element = (CompositeEditor) c.getChildren().get(c.getChildren().size() - 1);
			c.remove(element);
			filters.remove(element.getBinding().getObject());
			System.out.println(filters);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		Container element = (Container) getElement("f1");
		stageSelector = new ComboEnumeratedValueSelector<>();
		stageSelector.setCaption("Stage");

		visualizers = new ComboEnumeratedValueSelector<>();
		visualizers.setCaption("Visualizer");
		element.add(stageSelector);
		element.add(visualizers);
		analizers = new ComboEnumeratedValueSelector<>();
		analizers.setCaption("Analizers");
		element.add(analizers);
		stageSelector.getControl().addListener(SWT.Selection, (x) -> {
			update();
		});
		analizers.getControl().addListener(SWT.Selection, (x) -> {
			update();
		});
		visualizers.getControl().addListener(SWT.Selection, (x) -> {
			update();
		});
		ToolbarElement sl = new ToolbarElement();
		bindedAction = new Action(Action.AS_CHECK_BOX) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void run() {

				SashElement element2 = (SashElement) getElement("sl");
				SashForm control = element2.getControl();

				if (isChecked()) {
					control.setWeights(new int[] { 3, 1 });
				} else {
					control.setWeights(new int[] { 3, 0 });
				}

			}
		};
		bindedAction.setImageId("filter_x");
		bindedAction.setText("Filters");

		sl.getLayoutHints().setGrabHorizontal(true);
		sl.getLayoutHints().setAlignmentHorizontal(Alignment.RIGHT);
		sl.addToToolbar(bindedAction);
		Action bindedAction2 = new Action(Action.AS_CHECK_BOX) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			

			@Override
			public void run() {
				wrap=isChecked();
				if  (results!=null) {
				display(results);
				}

			}
		};
		bindedAction2.setImageId("wrap");
		bindedAction2.setText("Wrap");
		sl.addToToolbar(bindedAction2);
//		filter = new OneLineTextElement<>();
//		filter.setCaption("Filter");
//		//te.getLayoutHints().setAlignmentHorizontal(Alignment.RIGHT);
		element.add(sl);
	}
	private boolean wrap;
	class AnalizerOrVisualizerUI extends DynamicUI {

		private ArrayList<IntrospectedParameter> params;

		public AnalizerOrVisualizerUI(InstrospectedFeature feature) {
			super(feature);
			this.params = new ArrayList<IntrospectedParameter>();
			for (IntrospectedParameter p : feature.getParameters()) {
				if (p.getType() != null) {
					if (p.getType().equals("PredictionItem")) {
						continue;
					} else {
						if (p.getDefaultValue() != null) {
							this.params.add(p);
						}
					}
				}
			}
		}

		@Override
		protected ArrayList<IntrospectedParameter> getParameters() {
			return this.params;
		}

		public boolean requiresConfiguration() {
			return !params.isEmpty();
		}
	}

	protected void update() {
		Combo v = (Combo) visualizers.getControl();
		Combo a = (Combo) analizers.getControl();
		Combo s = (Combo) stageSelector.getControl();
		String visualizer = v.getText();
		String analizer = a.getText();
		String stage = s.getText();
		this.visualizationParams = new HashMap<>();
		this.analisisParams = new HashMap<>();
		if (!visualizer.isEmpty() && !analizer.isEmpty()) {

			visualizerFeature = this.options.getVisualizer(visualizer);
			InstrospectedFeature analizerFeature = this.options.getAnalizer(analizer);
			AnalizerOrVisualizerUI vui = new AnalizerOrVisualizerUI(visualizerFeature);
			AnalizerOrVisualizerUI vai = new AnalizerOrVisualizerUI(analizerFeature);
			vui.getArgs().putAll(this.visualizationParams);
			this.visualizationParams = vui.getArgs();
			vai.getArgs().putAll(this.analisisParams);
			this.analisisParams = vai.getArgs();
			if (vui.requiresConfiguration() || vai.requiresConfiguration()) {
				createConfig(vui, vai);
				return;
			} else {
				Container element = (Container) getElement("f2");
				new ArrayList<>(element.getChildren()).forEach(va -> element.remove(va));
			}
			getElement("empty").setEnabled(true);
			getElement("label").setCaption("Initial calculation is performed...");
			DataSetAnalisysRequest data = new DataSetAnalisysRequest(model, dataset,
					this.experiment.getPath().toOSString(), visualizer, analizer, this.isData, stage);
			data.setVisualizerArgs(new HashMap<>());
			data.setAnalzierArgs(new HashMap<>());
			data.setFilters(this.filters);
			task.perform(data, IAnalizeResults.class, (r) -> {
				display(r);
			}, (e) -> {
				onError(e);
			});
		} else {
			cleanContent();
		}
	}

	private void createConfig(AnalizerOrVisualizerUI vui, AnalizerOrVisualizerUI vai) {
		Container element = (Container) getElement("f2");
		new ArrayList<>(element.getChildren()).forEach(v -> element.remove(v));
		element.add(vui.populateParameters(visualizationParams, experiment));
		element.add(vai.populateParameters(analisisParams, experiment));
//		ButtonSelector element2 = new ButtonSelector();
//		element2.setCaption("Launch");
//
//		element.add(element2);
//		element2.getControl().addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
//				recalcView();
//			};
//		});
		element.getRoot().getControl().layout(true, true);
		// element.redraw();
		// element.getParent().getContentParent().layout();
		// element.getParent().getParent().getContentParent().layout();
		if (!bindedAction.isChecked()) {
			bindedAction.setChecked(true);
			bindedAction.run();
		}
	}

	private static Dataset createDataset(IAnalizeResults r, Object loadAs) {
		if (loadAs != null) {
			if (loadAs instanceof Map) {

				Map mm = (Map<String, Object>) loadAs;
				Object object = mm.get("values");
				Object total = mm.get("total");
				if (mm.get("type").equals("bar")) {
					ArrayList<Object> vls = (ArrayList<Object>) object;
					Map<String, Number> blds = (Map<String, Number>) vls.get(0);
					DefaultCategoryDataset cs = new DefaultCategoryDataset();
					for (String s : blds.keySet()) {
						Number number = blds.get(s);
						cs.addValue(number, s, "");
					}
					return cs;

				}
				if (object instanceof ArrayList) {
					ArrayList arrayList = (ArrayList) object;
					if (arrayList.get(0) instanceof Number){
						DefaultCategoryDataset dataset = new DefaultCategoryDataset();
						int size = arrayList.size();
						for (int i = 0; i < size; i++) {
							dataset.addValue((Number) arrayList.get(i),""+i,"");								
						}
						return dataset;
					}
				}
				DefaultXYDataset d = createXY(object, total);

				return d;
			}
			System.out.println(loadAs);
		}
		DefaultPieDataset dataset = new DefaultPieDataset();
		int size = r.size();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<Double> counts = new ArrayList<>();
		double sum = 0;
		for (int i = 0; i < size; i++) {
			IDataSet iDataSet = r.get(i);
			int len = iDataSet.len();
			String name = iDataSet.get_name();
			names.add(name);
			counts.add((double) len);
			sum = sum + len;

		}
		for (int i = 0; i < size; i++) {
			dataset.setValue(names.get(i) + "(" + counts.get(i).intValue() + " "
					+ NumberFormat.getPercentInstance().format(counts.get(i) / sum) + ")", counts.get(i));
		}
		return dataset;
	}

	public static DefaultXYDataset createXY(Object object, Object total) {
		DefaultXYDataset d = new DefaultXYDataset();
		String[] labels = new String[] { "All", "Positive", "Negative" };
		if (object instanceof ArrayList) {
			ArrayList x = (ArrayList) object;
			int num = 0;
			if (x.get(0) instanceof Double) {
				
			}
			for (Object z : x) {
				
				Map m = (Map) z;

				double[][] data = new double[2][m.size()];
				int i = 0;
				int sum = 0;
				for (Object o : m.keySet()) {
					data[0][i] = ((Integer) o).doubleValue();
					double doubleValue = ((Number) m.get(o)).doubleValue();
					data[1][i] = doubleValue;
					i = i + 1;
					sum += doubleValue;
				}
				if (total != null) {
					i = 0;
					for (Object o : m.keySet()) {
						data[1][i] = 1 - data[1][i] / ((Number) total).doubleValue();
						i = i + 1;
					}
				} else {
					i = 0;
					double sm = 0;
					for (Object o : m.keySet()) {
						sm = sm + data[1][i];
						data[1][i] = sm / sum;
						i = i + 1;
					}
				}
				d.addSeries(labels[num], data);
				num = num + 1;
			}
		}
		return d;
	}

	private static HistogramDataset createHistDataset(IAnalizeResults r) {
		HistogramDataset dataset = new HistogramDataset();
		int size = r.size();
		ArrayList<String> names = new ArrayList<>();
		double[] counts = new double[size];
		double sum = 0;
		for (int i = 0; i < size; i++) {
			IDataSet iDataSet = r.get(i);
			int len = iDataSet.len();
			String name = iDataSet.get_name();

			counts[i] = len;
			sum = sum + len;

		}
		dataset.addSeries("", counts, counts.length);
		return dataset;
	}

	private static JFreeChart createChart(Dataset dataset, Object loadAs) {
		if (dataset instanceof DefaultCategoryDataset) {
			String y_axis = "Fraction of samples";
			String x_axis = "Length";
			if (loadAs instanceof Map) {
				Map mp = (Map) loadAs;
				if (mp.containsKey("x_axis")) {
					x_axis = mp.get("x_axis").toString();
				}
				if (mp.containsKey("y_axis")) {
					y_axis = mp.get("y_axis").toString();
				}
			}
			JFreeChart chart = ChartFactory.createBarChart("", // Chart Title
					x_axis, // Category axis
					y_axis, // Value axis
					(CategoryDataset) dataset, PlotOrientation.VERTICAL, true, true, false);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			BarRenderer renderer = (BarRenderer) plot.getRenderer(0);
	        CategoryItemLabelGenerator generator 
	            = new StandardCategoryItemLabelGenerator("{1}", 
	                    NumberFormat.getInstance());
//	        renderer.setItemLabelGenerator(generator);
//	        renderer.setItemLabelFont(new Font("SansSerif", Font.PLAIN, 12));
//	        renderer.setItemLabelsVisible(true);
//	        renderer.setPositiveItemLabelPosition(new ItemLabelPosition(
//	                ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 
//	                - Math.PI / 2));
			return chart;
		}
		if (dataset instanceof PieDataset) {
			JFreeChart chart = ChartFactory.createPieChart("", // chart title
					(PieDataset) dataset, // data
					true, // include legend
					true, false);

			return chart;
		}
		if (dataset instanceof HistogramDataset) {
			JFreeChart chart = ChartFactory.createHistogram("", "", "", (HistogramDataset) dataset,
					PlotOrientation.VERTICAL, false, false, false);

			return chart;
		}
		if (dataset instanceof XYDataset) {
			String y_axis = "Fraction of samples";
			String x_axis = "Length";
			if (loadAs instanceof Map) {
				Map mp = (Map) loadAs;
				if (mp.containsKey("x_axis")) {
					x_axis = mp.get("x_axis").toString();
				}
				if (mp.containsKey("y_axis")) {
					y_axis = mp.get("y_axis").toString();
				}
			}
			JFreeChart chart = ChartFactory.createXYLineChart("", x_axis, y_axis, (XYDataset) dataset);

			return chart;
		}

		throw new IllegalStateException("Unknown dataset type");
	}

	boolean initedCharts;
	private Action bindedAction;
	private VisualizerViewer<?> visualizerViewer;
	private IAnalizeResults results;

	private void display(IAnalizeResults results) {
		this.results=results;
		getElement("empty").setEnabled(false);
		Container element = (Container) getElement("content");
		new ArrayList<>(element.getChildren()).forEach(v -> element.remove(v));
		String viewer = visualizerFeature.getViewer();
		visualizerViewer = null;
		if (viewer.equals("html")) {
			visualizerViewer = new VirtualTable();
			visualizerViewer.setHtml(true);
			visualizerViewer.setWrap(wrap);
		} else if (viewer.equals("image")) {
			visualizerViewer = new DataSetGallery();
		} else {
			visualizerViewer = new VirtualTable();
			visualizerViewer.setWrap(wrap);
		}
		visualizerViewer.getLayoutHints().setGrabHorizontal(true);
		visualizerViewer.getLayoutHints().setGrabVertical(true);
		element.setMargin(new Rectangle(0, 0, 0, 0));
		visualizerViewer.setInput(results);
		element.add(visualizerViewer);
		element.setEnabled(true);
		String visualizationSpec = results.visualizationSpec();
		Object loadAs = YamlIO.loadAs(visualizationSpec, Object.class);
		createChart = createChart(createDataset(results, loadAs), loadAs);
		element2 = (Container) getElement("stat");
		update(createChart, element2);
		if (!initedCharts) {
			ControlAdapter listener = new ControlAdapter() {

				public void controlResized(org.eclipse.swt.events.ControlEvent e) {
					update(createChart, element2);
				}
			};
			element2.getControl().addControlListener(listener);
			initedCharts = true;
		}

	}

	private void update(JFreeChart createChart, Container element2) {
		Point size = element2.getControl().getSize();
		int height = size.y - 3;
		if (size.x == 0 || height <= 0) {
			return;
		}
		BufferedImage bufferedImage = createChart.createBufferedImage(size.x, height);
		ImageData convertToSWT = ExperimentLogs.convertToSWT(bufferedImage);
		if (image != null) {
			image.dispose();
		}
		image = new Image(Display.getCurrent(), convertToSWT);
		element2.getControl().setBackgroundImage(null);
		element2.getControl().setBackgroundImage(image);
		element2.getControl().redraw();
	}

	private void cleanContent() {
		getElement("empty").setEnabled(true);
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
		}
		if (this.task != null) {
			this.task.terminate();
		}
		super.dispose();
	}

	@Override
	public void setFocus() {

	}

	public void setResults(IAnalizeResults r) {

	}

	public void setResults(GetPossibleAnalisisResult r, ModelEvaluationSpec model, String dataset,
			Experiment experiment, GateWayRelatedTask task, boolean isData) {
		if (this.task != null) {
			this.task.terminate();
		}
		getElement("label").setText("");
		this.model = model;
		this.dataset = dataset;
		this.experiment = experiment;
		this.options = r;
		this.isData = isData;
		this.inited = true;
		this.getBinding().refresh();
		Realm realm = new Realm(r.getVisualizers());
		datasetStages = r.getDatasetStages();
		this.filterKinds = r.getDatasetFilters();
		Realm stage = new Realm(datasetStages);
		this.stageSelector.setRealm(stage);
		visualizers.setRealm(realm);
		if (isData) {
			realm = new Realm(r.getData_analizers());
		} else {
			realm = new Realm(r.getAnalizers());
		}
		analizers.setRealm(realm);
		FormEditor de = (FormEditor) getRoot();
		de.setCaption(dataset + "(" + experiment.toString() + ")");
		
		if (datasetStages.size() > 0) {
			((Combo) stageSelector.getControl()).select(0);
			update();
		}
		
		this.task = task;
	}

	public void recalcView() {
		Combo v = (Combo) visualizers.getControl();
		Combo a = (Combo) analizers.getControl();
		Combo s = (Combo) stageSelector.getControl();
		String visualizer = v.getText();
		String analizer = a.getText();
		if (visualizer == null || visualizer.isEmpty()) {
			return;
		}
		if (analizer == null || analizer.isEmpty()) {
			return;
		}
		if (s.getText() == null || s.getText().isEmpty()) {
			return;
		}
		getElement("empty").setEnabled(true);
		getElement("label").setCaption("Initial calculation is performed...");
		DataSetAnalisysRequest data = new DataSetAnalisysRequest(model, dataset, experiment.getPath().toOSString(),
				visualizer, analizer, isData, s.getText());
		data.setVisualizerArgs(new LinkedHashMap<>(visualizationParams));
		data.setAnalzierArgs(new LinkedHashMap<>(analisisParams));
		data.setFilters(filters);

		task.perform(data, IAnalizeResults.class, (r) -> {
			display(r);
		}, (e) -> {
			onError(e);
		});
	}

	private void onError(Throwable e) {
		org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openError(org.eclipse.swt.widgets.Display.getCurrent().getActiveShell(), "Error",
						e.getMessage());
			}
		});
	}

}