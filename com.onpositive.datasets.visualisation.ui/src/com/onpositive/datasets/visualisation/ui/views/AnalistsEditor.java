package com.onpositive.datasets.visualisation.ui.views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jfree.chart.JFreeChart;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SashElement;
import com.onpositive.commons.elements.ToolbarElement;
import com.onpositive.dataset.visualization.internal.DataSetGallery;
import com.onpositive.dataset.visualization.internal.Utils;
import com.onpositive.dataset.visualization.internal.VirtualTable;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.actions.BasicImageDataSetActions.ConversionAction;
import com.onpositive.musket.data.images.actions.BasicImageDataSetActions.ConvertResolutionAction;
import com.onpositive.musket.data.images.actions.BasicImageDataSetActions.GenerateDataSetAction;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;
import com.onpositive.semantic.model.ui.property.editors.FormTextElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.semantic.ui.core.Alignment;
import com.onpositive.semantic.ui.core.Rectangle;
import com.onpositive.semantic.ui.workbench.elements.XMLEditorPart;

public abstract class AnalistsEditor extends XMLEditorPart {

	private String dataset;
	private PossibleAnalisisSpec options;
	protected ComboEnumeratedValueSelector<Object> visualizers;
	protected ComboEnumeratedValueSelector<Object> analizers;
	private boolean isData;
	private HashMap<String, Object> visualizationParams = new HashMap<>();
	private HashMap<String, Object> analisisParams = new HashMap<>();
	private Image image;
	private InstrospectedFeature visualizerFeature;
	// private ComboEnumeratedValueSelector<String> stage;

	protected ArrayList<DataSetFilter> filters = new ArrayList<>();
	private ArrayList<String> datasetStages;
	private ArrayList<InstrospectedFeature> filterKinds = new ArrayList<>();

	private ArrayList<CompositeEditor> filterUIs = new ArrayList<>();
	private JFreeChart createChart;
	private Container element2;

	public AnalistsEditor() {
		super();
	}

	@Override
	protected String getUIDefinitionPath() {
		return "dlf/analisysView.dlf";
	}

	public void addFilter() {
		DataSetFilter object = new DataSetFilter();
		object.setMode("normal");
		object.getModes().add("normal");
		object.getModes().add("inverse");
		object.getKinds().addAll(filterKinds.stream().map(x -> x.getName()).collect(Collectors.toList()));
		object.getStages().addAll(datasetStages);
		if (datasetStages.size() > 0) {
			object.setApplyAt(datasetStages.get(datasetStages.size() - 1));
		}
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

		visualizers = new ComboEnumeratedValueSelector<>();
		visualizers.setCaption("Visualizer");
		visualizers.setSelectDefault(true);

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
				wrap = isChecked();
				if (results != null) {
					display(results);
				}

			}
		};
		bindedAction2.setImageId("wrap");
		bindedAction2.setText("Wrap");

		generatorMenu = new MA();
		generatorMenu.setImageId("generic_task");
		generatorMenu.setText("Generate");
		generatorMenu.setVisible(true);

		sl.addToToolbar(bindedAction2);
		sl.addToToolbar(generatorMenu);
		
		focus = new Action(Action.AS_CHECK_BOX) {
			
			@Override
			public void run() {
				focusOn();				
			}

			
		};
		
		generatorMenu.setImageId("generic_task");
		focus.setText("Focus on");
		focus.setEnabled(false);
		focus.setImageId("focus_on");
		sl.addToToolbar(focus);
//		filter = new OneLineTextElement<>();
//		filter.setCaption("Filter");
//		//te.getLayoutHints().setAlignmentHorizontal(Alignment.RIGHT);
		element.add(sl);
	}

	protected void focusOn() {
		
	}

	private boolean wrap;

	private final class MA extends Action {
		/**
		 * 
		 */

		public MA() {
			super(Action.AS_DROP_DOWN_MENU);
		}

		private static final long serialVersionUID = 1L;

		@Override
		public void run() {
			if (results != null) {
				
				ActionSelection actionSelection = new ActionSelection(results.getOriginal().conversions());
				boolean createObject = WidgetRegistry.createObject(actionSelection);
				if (createObject) {
					String targetFile = actionSelection.targetFile();
					ConversionAction selectedAction = actionSelection.getSelectedAction();
					if (selectedAction instanceof GenerateDataSetAction) {
						GenerateDataSetAction fm = (GenerateDataSetAction) selectedAction;
						String name = targetFile;
						new DataSetGenerator().generateDataSet(results.getOriginal(), getInputFile(), name, true,
								getProject());
						IFile file = getProject().getFile("common.yaml");
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
									new FileEditorInput(file),
									"com.onpositive.dside.ui.editors.ExperimentMultiPageEditor");
							file = getProject().getFolder("modules").getFile("datasets.py");
							IEditorDescriptor defaultEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
									new FileEditorInput(file),
									defaultEditor.getId());
							afterDataSetCreate(name,results.getOriginal());
							return;
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (selectedAction instanceof ConvertResolutionAction) {
						String[] split = targetFile.split(",");
						try {
							int width = Integer.parseInt(split[0].trim());
							int height = Integer.parseInt(split[1].trim());
							Job s = new Job("Convert images") {

								@Override
								protected IStatus run(IProgressMonitor monitor) {

									selectedAction.perform(results.getOriginal(), width, height,
											new com.onpositive.musket.data.core.IProgressMonitor() {

												@Override
												public boolean onProgress(String message, int passsedTicks) {
													monitor.worked(passsedTicks);
													return true;
												}

												@Override
												public boolean onDone(String message, int totalTicks) {
													monitor.done();
													return true;
												}

												@Override
												public boolean onBegin(String message, int totalTicks) {
													monitor.beginTask("Converting images", totalTicks);
													return true;

												}
											});
									return Status.OK_STATUS;
								}
							};
							s.schedule();

							return;
						} catch (NumberFormatException e) {
							MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
									"Target should be width, height");
						}
					}
					File actualTarget = getActualTarget(targetFile);
					selectedAction.run(results.getOriginal(), actualTarget);
					IFile[] findFilesForLocationURI = ResourcesPlugin.getWorkspace().getRoot()
							.findFilesForLocationURI(actualTarget.toURI());
					for (IFile f : findFilesForLocationURI) {
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
									new FileEditorInput(f), "com.onpositive.datasets.visualisation.ui.datasetEditor");
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

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
				// return;
			} else {
				Container element = (Container) getElement("f2");
				new ArrayList<>(element.getChildren()).forEach(va -> element.remove(va));
			}
			getElement("empty").setEnabled(true);
			getElement("label").setCaption("Initial calculation is performed...");
			DataSetAnalisysRequest data = new DataSetAnalisysRequest(null, dataset, "", visualizer, analizer,
					this.isData, "");
			data.setVisualizerArgs(new HashMap<>());
			data.setAnalzierArgs(new HashMap<>());
			data.setFilters(this.filters);
			task.perform(data, (r) -> {
				display(r);
			}, (e) -> {
				onError(e);
			});
		} else {
			cleanContent();
		}
	}

	public void afterDataSetCreate(String name,IDataSet original) {
		
	}

	public abstract File getInputFile();

	protected abstract IProject getProject();

	protected abstract File getActualTarget(String targetFile);

	private void createConfig(AnalizerOrVisualizerUI vui, AnalizerOrVisualizerUI vai) {
		Container element = (Container) getElement("f2");
		new ArrayList<>(element.getChildren()).forEach(v -> element.remove(v));
		element.add(vui.populateParameters(visualizationParams, null));
		element.add(vai.populateParameters(analisisParams, null));
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

	boolean initedCharts;
	private Action bindedAction;
	private com.onpositive.dataset.visualization.internal.VisualizerViewer<?> g;
	private IAnalizeResults results;

	private void display(IAnalizeResults r) {
		this.results = r;
		getElement("empty").setEnabled(false);
		boolean b = results.getOriginal() instanceof IMulticlassClassificationDataSet;
		focus.setEnabled(b||focus.isChecked());
		Container element = (Container) getElement("content");
		new ArrayList<>(element.getChildren()).forEach(v -> element.remove(v));
		String viewer = visualizerFeature.getViewer();
		if (viewer == null) {
			viewer = "image";
		}
		g = null;
		if (viewer.equals("html")) {
			VirtualTable v = new VirtualTable();
			v.setHtml(true);
			v.setWrap(wrap);
			g = v;

		} else if (viewer.equals("image")) {
			g = new DataSetGallery();
		} else {
			VirtualTable v = new VirtualTable();
			v.setWrap(wrap);
			g = v;
		}
		g.getLayoutHints().setGrabHorizontal(true);
		g.getLayoutHints().setGrabVertical(true);
		element.setMargin(new Rectangle(0, 0, 0, 0));
		g.setInput(r);
		element.add(g);
		element.setEnabled(true);
		String visualizationSpec = r.visualizationSpec();
		// Object loadAs = new Yaml().loadAs(visualizationSpec, Object.class);
		createChart = ChartUtils.createChart(ChartUtils.createDataset(r, null), null);
		element2 = (Container) getElement("stat");

		FormTextElement<?> labels = (FormTextElement) getElement("info");

		List<DescriptionEntry> description = results.getOriginal().description();

		StringBuilder bld = new StringBuilder();
		description.forEach(v -> {
			bld.append("<b>" + v.getCaption() + ":</b> " + v.getStringValue() + "; ");

		});
//		for (IContributionItem i:generatorMenu.getItems()) {
//			generatorMenu.remove(i);
//		}
//		List<ConversionAction> conversions = results.getOriginal().conversions();
//		conversions.forEach(v->{
//			Action action = new Action() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					
//				}
//			};
//			action.setText(v.getCaption());
//			generatorMenu.add(action);
//		});
		labels.setText(bld.toString());
		try {
			update(createChart, element2);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
		}
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

	@SuppressWarnings("unused")
	private void update(JFreeChart createChart, Container element2) {
		Point size = element2.getControl().getSize();
		BufferedImage createBufferedImage = createChart.createBufferedImage(size.x, size.y - 3);
		ImageData convertToSWT = Utils.convertToSWT(createBufferedImage);
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

	IAnalisysEngine task;
	private Action generatorMenu;
	protected Action focus;

	public void setEngine(IAnalisysEngine engine) {
		if (this.task != null) {
			this.task.terminate();
		}
		getElement("label").setText("");
		PossibleAnalisisSpec r = engine.getSpec();
		// this.model = model;
		this.dataset = dataset;
		// this.experiment = experiment;
		this.options = r;
		this.isData = isData;
		this.getBinding().refresh();
		Realm realm = new Realm(r.getVisualizers());
		datasetStages = r.getDatasetStages();
		this.filterKinds = r.getDatasetFilters();
		visualizers.setRealm(realm);
		if (isData) {
			realm = new Realm(r.getData_analizers());
		} else {
			realm = new Realm(r.getAnalizers());
		}
		analizers.setRealm(realm);
		FormEditor de = (FormEditor) getUIRoot();
		// de.setCaption(dataset + "(" + experiment.toString() + ")");
		this.task = engine;
	}

	public void recalcView() {
		Combo v = (Combo) visualizers.getControl();
		Combo a = (Combo) analizers.getControl();
		String visualizer = v.getText();
		String analizer = a.getText();
		if (visualizer == null || visualizer.isEmpty()) {
			return;
		}
		if (analizer == null || analizer.isEmpty()) {
			return;
		}
		getElement("empty").setEnabled(true);
		getElement("label").setCaption("Initial calculation is performed...");
		DataSetAnalisysRequest data = new DataSetAnalisysRequest(null, dataset, "", visualizer, analizer, isData, "");
		data.setVisualizerArgs(new LinkedHashMap<>(visualizationParams));
		data.setAnalzierArgs(new LinkedHashMap<>(analisisParams));
		data.setFilters(filters);

		task.perform(data, (r) -> {
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