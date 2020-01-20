package com.onpositive.datasets.visualisation.ui.views;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.part.MultiEditorInput;
import org.eclipse.ui.progress.UIJob;
import org.python.pydev.shared_ui.EditorUtils;

import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.LinkElement;
import com.onpositive.datasets.engine.AnalisysEngine;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IFilterProto;
import com.onpositive.musket.data.core.IProgressMonitor;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.images.AbstractImageDataSet;
import com.onpositive.musket.data.images.BinaryClassificationDataSet;
import com.onpositive.musket.data.images.BinarySegmentationDataSet;
import com.onpositive.musket.data.images.FolderDataSet;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IImageItem;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.MultiClassSegmentationDataSet;
import com.onpositive.musket.data.images.MultiClassificationDataset;
import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.project.DataProjectAccess;
import com.onpositive.musket.data.table.ICSVOVerlay;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.text.TextClassificationDataSet;
import com.onpositive.musket.data.text.TextSequenceDataSet;
import com.onpositive.semantic.model.ui.generic.HyperlinkEvent;
import com.onpositive.semantic.model.ui.generic.IHyperlinkListener;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class CSVDataSetEditor extends AnalistsEditor {

	private final class PM implements IProgressMonitor {
		private final org.eclipse.core.runtime.IProgressMonitor monitor;

		private PM(org.eclipse.core.runtime.IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
		public boolean onProgress(String message, int passsedTicks) {
			monitor.beginTask(message, 10);
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return false;
			}
			return true;
		}

		@Override
		public boolean onDone(String message, int totalTicks) {
			monitor.done();
			if (monitor.isCanceled()) {
				return false;
			}
			return true;

		}

		@Override
		public boolean onBegin(String message, int totalTicks) {
			monitor.beginTask(message, 10);
			if (monitor.isCanceled()) {
				return false;
			}
			return true;
		}
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.onpositive.datasets.visualisation.ui.views.SampleView";

	@Inject
	IWorkbench workbench;

	private File file2;

	private IDataSet ds;

	private IProject project;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			File file3 = fromINput(editorInput);
			file2 = file3;
			Job job = new Job("Opening dataset") {

				@Override
				protected IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor) {
					ds = DataProjectAccess.getDataSet(file2, new BasicQuestionAnswerer(), new PM(monitor),encoding);
					if (ds != null) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								init();
							}
						});
					}
					return Status.OK_STATUS;
				}
			};
			// job.setUser(true);

			showInUI(job);

		}
		if (editorInput instanceof FolderEditorInput) {
			FolderEditorInput ed=(FolderEditorInput) editorInput;
			File file = ed.getFolder().getLocation().toFile();
			file2=file;
			IDataSet createDataSetFromFolder;
			try {
				createDataSetFromFolder = FolderDataSet.createDataSetFromFolder(file, ed.getFolder().getDefaultCharset());
				this.ds=createDataSetFromFolder;
				project=ed.getFolder().getProject();
				init();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if (editorInput instanceof MultiEditorInput) {
			IEditorInput[] input = ((MultiEditorInput) editorInput).getInput();
			File f1 = fromINput(input[0]);
			File f2 = fromINput(input[1]);
			file2 = f1;
			IDataSet dataSet = DataProjectAccess.getDataSet(f1, new BasicQuestionAnswerer(), new IProgressMonitor() {

				@Override
				public boolean onProgress(String message, int passsedTicks) {
					return true;
				}

				@Override
				public boolean onDone(String message, int totalTicks) {
					return true;
				}

				@Override
				public boolean onBegin(String message, int totalTicks) {
					return true;
				}

			},encoding);
			ds = dataSet.withPredictions(f2);
			setPartName(f1.getName() + "-" + f2.getName());
			init();

		}
	}

	protected void showInUI(Job job) {
		boolean boolean1 = WorkbenchPlugin.getDefault().getPreferenceStore()
				.getBoolean(IPreferenceConstants.RUN_IN_BACKGROUND);
		try {
			job.schedule();
			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.RUN_IN_BACKGROUND, false);

			PlatformUI.getWorkbench().getProgressService().showInDialog(Display.getCurrent().getActiveShell(), job);
		} finally {
			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.RUN_IN_BACKGROUND,
					boolean1);
		}

		this.setPartName(file2.getName());
	}

	@Override
	protected void focusOn() {
		if (isFocused) {
			AnalisysEngine engine = new AnalisysEngine(ds);
			focus.setText("Focus on");
			initWithEngine(engine);
			isFocused = false;
			return;
		}
		if (ds instanceof GenericDataSet) {
			IFilterProto[] filters2 = ds.filters();

			ArrayList<InstrospectedFeature> protos = new ArrayList<>();
			for (InstrospectedFeature p : task.getSpec().getDatasetFilters()) {
				Supplier<Collection<String>> values = p.getValues();
				if (values != null) {

					if (p.getName().contains("is less") || p.getName().contains("is more")) {
						continue;
					}
					protos.add(p);
				}
			}
			DataSetFilter2 f2 = new DataSetFilter2(protos);

			boolean createObject = WidgetRegistry.createObject(f2);
			if (createObject) {
				DataSetAnalisysRequest data = new DataSetAnalisysRequest();
				data.getFilters().add(f2);
				task.filter(data, x -> {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							AnalisysEngine engine = new AnalisysEngine(x);
							initWithEngine(engine);
							isFocused = true;
							focus.setText("Unfocus");
						}
					});

				}, e -> {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							MessageDialog.openError(Display.getCurrent().getActiveShell(), e.getMessage(),
									e.getMessage());
						}
					});

				});
			} else {
				focus.setChecked(false);

			}
		}
		 else

			{
				FocusOnModel mod=new FocusOnModel();
				IMulticlassClassificationDataSet d = (IMulticlassClassificationDataSet) ds;
				mod.classes=d.classNames();
				boolean createObject = WidgetRegistry.createObject(mod);
				if (createObject) {
					
					
					IBinaryClassificationDataSet forClass = d.forClass(mod.input);
					AnalisysEngine engine = new AnalisysEngine(forClass);
					initWithEngine(engine);
					isFocused = true;
					focus.setText("Unfocus");
				}
				else {
					isFocused=false;
					focus.setChecked(false);
					super.focusOn();
				}
				// focus.setImageId("generic_task");
							
			}
		super.focusOn();
	}

	boolean isFocused;

	boolean uiPatched;

	private String encoding;

	private void init() {
		if (ds == null) {
			((Container) getUIRoot()).getElement("sl").setEnabled(false);
			((Container) getUIRoot()).getElement("label")
					.setText("Sorry, we do not understand this kind of dataset yet");

			return;
		}
		if (!(ds instanceof GenericDataSet)) {
			if (!(ds instanceof IDataSetWithGroundTruth)) {

				if (ds instanceof ICSVOVerlay && file2 != null) {
					if (!uiPatched) {
						uiPatched = true;
						Container cm = (Container) ((Container) getUIRoot()).getElement("f1");
						LinkElement linkElement = new LinkElement();
						linkElement.getLayoutHints().setGrabHorizontal(false);
						linkElement.setText("Switch to generic");
						linkElement.addHyperLinkListener(new IHyperlinkListener() {

							@Override
							public void linkExited(HyperlinkEvent arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void linkEntered(HyperlinkEvent arg0) {
								// TODO Auto-generated method stub

							}

							IDataSet base;

							@Override
							public void linkActivated(HyperlinkEvent arg0) {
								if (base == null) {
									base = ds;
									ICSVOVerlay vv = (ICSVOVerlay) ds;
									ITabularDataSet original = vv.original();
									DataProject project2 = DataProjectAccess.getProject(file2.getParentFile());
									Job job = new Job("Opening dataset") {

										@Override
										protected IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor) {
											ds = project2.openGeneric(original, new BasicQuestionAnswerer(),
													new PM(monitor));
											if (ds != null) {
												Display.getDefault().asyncExec(new Runnable() {

													@Override
													public void run() {
														linkElement.setText("Switch to specialized");
														init();
													}
												});
											}
											return Status.OK_STATUS;
										}
									};
									// job.setUser(true);

									showInUI(job);
								} else {
									ds = base;
									base = null;
									init();
									linkElement.setText("Switch to generic");
								}

								// now we need to get base dataset from this;
							}
						});
						cm.add(linkElement);
						cm.getContentParent().layout();
					}
				}
			}
		}
		AnalisysEngine engine = new AnalisysEngine(ds);

		initWithEngine(engine);
	}

	protected void initWithEngine(AnalisysEngine engine) {
		visualizers.setSelectDefault(true);
		super.setEngine(engine);
		Combo control = (Combo) visualizers.getControl();
		control.setText(engine.getSpec().getVisualizers().get(0).name);

		control = (Combo) analizers.getControl();
		control.setText(engine.getSpec().getAnalizers().get(0).name);
		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run() {
				update();
			}
		});
	}
	

	private File fromINput(IEditorInput editorInput) {
		IFileEditorInput input = (IFileEditorInput) editorInput;
		IFile file = input.getFile();
		try {
			this.encoding=file.getCharset();
		} catch (CoreException e) {
			this.encoding="UTF-8";
		}
		this.project = file.getProject();
		File file3 = file.getLocation().toFile();
		return file3;
	}

	@Override
	public void recalcView() {
		super.recalcView();
		DataProjectAccess.updateMeta(file2, ds);
	}

	@Override
	public void afterDataSetCreate(String name, IDataSet original) {
		GenericExperimentTemplate temp = null;
		if (original instanceof BinaryClassificationDataSet) {
			temp = new ClassificationTemplate();
		}
		if (original instanceof BinarySegmentationDataSet) {
			temp = new SegmentationTemplate();
		}
		if (original instanceof MultiClassSegmentationDataSet) {
			temp = new SegmentationTemplate();
		}
		if (original instanceof TextClassificationDataSet) {
			temp = new TextClassificationTemplate();
		}
		if (original instanceof TextSequenceDataSet) {
			temp = new TextSequenceTemplate();
		}
		if (temp != null) {
			temp.projectPath = project.getLocation().toFile().getAbsolutePath();
			boolean openQuestion = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Please confirm",
					"Great, you have a dataset now, may be you want to configure an experiment?");
			if (openQuestion) {
				configureFromDataSetAndTemplate(project, "", original, temp, "get_" + name + ": []");
			}
		}
	}

	public static void configureFromDataSetAndTemplate(IProject project, String name, IDataSet original,
			GenericExperimentTemplate temp, String dsName) {
		GenericExperimentTemplate classificationTemplate = temp;
		temp.projectPath = project.getLocation().toFile().getAbsolutePath();

		classificationTemplate.activation = "sigmoid";
		classificationTemplate.numClasses = 1;
		classificationTemplate.name = name;
		if (original != null) {

			if (classificationTemplate instanceof ImageExperimentTemplate) {
				AbstractImageDataSet<IImageItem> it = (AbstractImageDataSet<IImageItem>) original;
				Image image = it.item(0).getImage();
				((ImageExperimentTemplate) classificationTemplate).width = image.getWidth(null);
				((ImageExperimentTemplate) classificationTemplate).height = image.getHeight(null);
			}
			if (original instanceof IMulticlassClassificationDataSet) {
				List<String> classNames = ((IMulticlassClassificationDataSet) original).classNames();
				int size = classNames.size();
				boolean b = ((IMulticlassClassificationDataSet) original).isExclusive();
				if (size > 2 && !b) {
					if (classNames.contains("Empty")) {
						classificationTemplate.numClasses = size-1;
					}
					else {
						classificationTemplate.numClasses = size;
					}
					if (((IMulticlassClassificationDataSet) original).isExclusive()) {
						classificationTemplate.activation = "softmax";
					}
				}
				if (size>2&&b) {
					classificationTemplate.numClasses = size;
				}
			}
			if (classificationTemplate instanceof TextSequenceTemplate) {
				TextSequenceTemplate ts=(TextSequenceTemplate) classificationTemplate;
				if (ts.add_crf) {
					classificationTemplate.activation="softmax";
				}
				else{
					classificationTemplate.activation="crf_loss";
				}
				classificationTemplate.numClasses=((TextSequenceDataSet)original).lastClassCount();
			}
		}
		boolean createObject = WidgetRegistry.createObject(classificationTemplate);
		if (createObject) {
			String finish = classificationTemplate.finish();
			finish = finish.replace("{dataset}", dsName);
			IFolder folder = project.getFolder("experiments");
			IFolder folder2 = folder.getFolder(classificationTemplate.name);
			try {
				if (!folder2.exists()) {
					folder2.create(true, true, new NullProgressMonitor());
				}
				IFile file = folder2.getFile("config.yaml");

				if (file.exists()) {
					file.setContents(new ByteArrayInputStream(finish.getBytes()), true, true,
							new NullProgressMonitor());
				} else {
					file.create(new ByteArrayInputStream(finish.getBytes()), true, new NullProgressMonitor());
				}
				Display.getDefault().asyncExec(() -> {
					EditorUtils.openFile(file);
				});
			} catch (Exception e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
			}
			classificationTemplate.finishExperimentFolder(folder2);
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	protected File getActualTarget(String targetFile) {
		return new File(file2.getParentFile(), targetFile);
	}

	@Override
	protected IProject getProject() {
		return project;
	}

	@Override
	public File getInputFile() {
		return file2;
	}
	
	@Override
	public boolean isDirty() {
		return false;
	}

}