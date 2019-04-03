package com.onpositive.dside.tasks.analize;

import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.onpositive.commons.elements.Container;
import com.onpositive.dside.dto.DataSetAnalisysRequest;
import com.onpositive.dside.dto.GetPossibleAnalisisResult;
import com.onpositive.dside.dto.introspection.InstrospectedFeature;
import com.onpositive.dside.dto.introspection.IntrospectedParameter;
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
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;
import com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector;
import com.onpositive.semantic.ui.core.Rectangle;
import com.onpositive.semantic.ui.workbench.elements.XMLView;

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

	public AnalistsView() {
		super("dlf/analisysView.dlf");
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		Container element = (Container) getElement("f1");
		visualizers = new ComboEnumeratedValueSelector<>();
		visualizers.setCaption("Visualizer");
		element.add(visualizers);
		analizers = new ComboEnumeratedValueSelector<>();
		analizers.setCaption("Analizers");
		element.add(analizers);

		analizers.getControl().addListener(SWT.Selection, (x) -> {
			update();
		});
		visualizers.getControl().addListener(SWT.Selection, (x) -> {
			update();
		});

	}

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
		String visualizer = v.getText();
		String analizer = a.getText();
		this.visualizationParams=new HashMap<>();
		this.analisisParams=new HashMap<>();
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
			}
			else {
				Container element = (Container) getElement("f2");
				new ArrayList<>(element.getChildren()).forEach(va -> element.remove(va));
			}
			getElement("empty").setEnabled(true);
			getElement("label").setCaption("Initial calculation is performed...");
			DataSetAnalisysRequest data = new DataSetAnalisysRequest(model, dataset,
					this.experiment.getPath().toOSString(), visualizer, analizer, this.isData);
			data.setVisualizerArgs(new HashMap<>());
			data.setAnalzierArgs(new HashMap<>());
			task.perform(data, IAnalizeResults.class, (r) -> {
				display(r);
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
		ButtonSelector element2 = new ButtonSelector();
		element2.setCaption("Launch");

		element.add(element2);
		element2.getControl().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Combo v = (Combo) visualizers.getControl();
				Combo a = (Combo) analizers.getControl();
				String visualizer = v.getText();
				String analizer = a.getText();
				getElement("empty").setEnabled(true);
				getElement("label").setCaption("Initial calculation is performed...");
				DataSetAnalisysRequest data = new DataSetAnalisysRequest(model, dataset,
						experiment.getPath().toOSString(), visualizer, analizer, isData);
				data.setVisualizerArgs(new LinkedHashMap<>(visualizationParams));
				data.setAnalzierArgs(new LinkedHashMap<>(analisisParams));
				task.perform(data, IAnalizeResults.class, (r) -> {
					display(r);
				});
			};
		});
		element.getRoot().getControl().layout(true, true);
		// element.redraw();
		// element.getParent().getContentParent().layout();
		// element.getParent().getParent().getContentParent().layout();
	}

	private static PieDataset createDataset(IAnalizeResults r) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		int size = r.size();
		ArrayList<String>names=new ArrayList<>();
		ArrayList<Double>counts=new ArrayList<>();
		double sum=0;
		for (int i=0;i<size;i++) {
			IDataSet iDataSet = r.get(i);
			int len = iDataSet.len();
			String name = iDataSet.name();
			names.add(name);
			counts.add((double)len);
			sum=sum+len;
			
		}	
		for (int i=0;i<size;i++) {
			dataset.setValue(names.get(i)+"("+counts.get(i).intValue()+" "+NumberFormat.getPercentInstance().format(counts.get(i)/sum)+")",counts.get(i));
		}
		return dataset;
	}

	private static JFreeChart createChart(PieDataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart("", // chart title
				dataset, // data
				true, // include legend
				true, false);

		return chart;
	}

	private void display(IAnalizeResults r) {
		getElement("empty").setEnabled(false);
		Container element = (Container) getElement("content");
		new ArrayList<>(element.getChildren()).forEach(v -> element.remove(v));
		String viewer = visualizerFeature.getViewer();
		VisualizerViewer<?> g =null;
		if (viewer.equals("html")) {
			g=new VirtualTable();
			g.setHtml(true);
		}		
		else if (viewer.equals("image")) {
			g=new DataSetGallery();
		}
		else {
			g=new VirtualTable();
		}
		g.getLayoutHints().setGrabHorizontal(true);
		g.getLayoutHints().setGrabVertical(true);
		element.setMargin(new Rectangle(0, 0, 0, 0));
		g.setInput(r);
		element.add(g);
		element.setEnabled(true);
		JFreeChart createChart = createChart(createDataset(r));
		Container element2 = (Container) getElement("stat");
		update(createChart, element2);
		element2.getControl().addControlListener(new ControlAdapter() {
			
			public void controlResized(org.eclipse.swt.events.ControlEvent e) {
				update(createChart, element2);
			}
		});
	}

	private void update(JFreeChart createChart, Container element2) {
		Point size = element2.getControl().getSize();
		BufferedImage createBufferedImage = createChart.createBufferedImage(size.x, size.y-3);
		ImageData convertToSWT = ExperimentLogs.convertToSWT(createBufferedImage);
		if (image!=null) {
			image.dispose();
		}
		image = new Image(Display.getCurrent(), convertToSWT);	
		element2.getControl().setBackgroundImage(image);
		element2.getControl().redraw();
	}

	private void cleanContent() {
		getElement("empty").setEnabled(true);
	}

	@Override
	public void dispose() {
		if (image!=null) {
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
		if (this.task!=null) {
			this.task.terminate();
		}
		this.model = model;
		this.dataset = dataset;
		this.experiment = experiment;
		this.options = r;
		this.isData = isData;
		this.inited = true;
		this.getBinding().refresh();
		Realm realm = new Realm(r.getVisualizers());
		visualizers.setRealm(realm);
		if (isData) {
			realm = new Realm(r.getData_analizers());
		} else {
			realm = new Realm(r.getAnalizers());
		}
		analizers.setRealm(realm);
		FormEditor de = (FormEditor) getRoot();
		de.setCaption(dataset + "(" + experiment.toString() + ")");
		this.task = task;
	}

}
