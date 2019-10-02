package com.onpositive.datasets.visualisation.ui.views;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.ui.part.MultiEditorInput;
import org.python.pydev.shared_ui.EditorUtils;

import com.onpositive.commons.elements.Container;
import com.onpositive.datasets.engine.AnalisysEngine;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.AbstractImageDataSet;
import com.onpositive.musket.data.images.BinaryClassificationDataSet;
import com.onpositive.musket.data.images.BinarySegmentationDataSet;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IImageItem;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.MultiClassSegmentationDataSet;
import com.onpositive.musket.data.images.MultiClassificationDataset;
import com.onpositive.musket.data.project.DataProjectAccess;
import com.onpositive.musket.data.text.TextClassificationDataSet;
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
			ds = DataProjectAccess.getDataSet(file2,new BasicQuestionAnswerer());
			this.setPartName(file3.getName());
			init();

		}
		if (editorInput instanceof MultiEditorInput) {
			IEditorInput[] input = ((MultiEditorInput) editorInput).getInput();
			File f1 = fromINput(input[0]);
			File f2 = fromINput(input[1]);
			file2 = f1;
			IDataSet dataSet = DataProjectAccess.getDataSet(f1,new BasicQuestionAnswerer());
			ds = dataSet.withPredictions(f2);
			setPartName(f1.getName() + "-" + f2.getName());
			init();

		}
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
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "Please select class",
				"Please select class", "", new IInputValidator() {

					@Override
					public String isValid(String newText) {
						IMulticlassClassificationDataSet d = (IMulticlassClassificationDataSet) ds;
						boolean contains = d.classNames().contains(newText);
						return contains ? null : "Please select valid class name";
					}
				});
		int open = dlg.open();
		if (open == Dialog.OK) {
			{
				IMulticlassClassificationDataSet d = (IMulticlassClassificationDataSet) ds;
				IBinaryClassificationDataSet forClass = d.forClass(dlg.getValue());
				AnalisysEngine engine = new AnalisysEngine(forClass);
				initWithEngine(engine);
				isFocused = true;
				focus.setText("Unfocus");
			}
			// focus.setImageId("generic_task");
		} else {
			focus.setChecked(false);
		}
		super.focusOn();
	}

	boolean isFocused;

	private void init() {
		if (ds==null) {
			((Container)getUIRoot()).getElement("sl").setEnabled(false);
			return;
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
		if (temp != null) {
			temp.projectPath = project.getLocation().toFile().getAbsolutePath();
			boolean openQuestion = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Please confirm",
					"Great, you have a dataset now, may be you want to configure an experiment?");
			if (openQuestion) {
				configureFromDataSetAndTemplate(project, "", original, temp, "get" + name + ": []");
			}
		}
	}

	public static void configureFromDataSetAndTemplate(IProject project, String name, IDataSet original,
			GenericExperimentTemplate temp, String dsName) {
		GenericExperimentTemplate classificationTemplate = temp;
		temp.projectPath = project.getLocation().toFile().getAbsolutePath();
		if (original != null) {

			if (classificationTemplate instanceof ImageExperimentTemplate) {
				AbstractImageDataSet<IImageItem> it = (AbstractImageDataSet<IImageItem>) original;
				Image image = it.item(0).getImage();
				((ImageExperimentTemplate) classificationTemplate).width = image.getWidth(null);
				((ImageExperimentTemplate) classificationTemplate).height = image.getHeight(null);
			}
			classificationTemplate.activation = "sigmoid";
			classificationTemplate.numClasses = 1;
			classificationTemplate.name = name;
			if (original instanceof IMulticlassClassificationDataSet) {
				int size = ((IMulticlassClassificationDataSet) original).classNames().size();
				if (size > 2 && !(((IMulticlassClassificationDataSet) original).isExclusive())) {
					classificationTemplate.numClasses = size;
					if (((IMulticlassClassificationDataSet) original).isExclusive()) {
						classificationTemplate.activation = "softmax";
					}
				}
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

}