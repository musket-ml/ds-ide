package com.onpositive.datasets.visualisation.ui.views;
import java.io.File;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.MultiEditorInput;

import com.onpositive.datasets.engine.AnalisysEngine;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.project.DataProjectAccess;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class CSVDataSetEditor extends AnalistsEditor {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.onpositive.datasets.visualisation.ui.views.SampleView";

	@Inject IWorkbench workbench;

	private File file2;

	private IDataSet ds;
	 

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			File file3 = fromINput(editorInput);
			file2 = file3;
			ds = DataProjectAccess.getDataSet(file2);
			
			
			init();
			
		}
		if (editorInput instanceof MultiEditorInput) {
			IEditorInput[] input = ((MultiEditorInput) editorInput).getInput();
			File f1=fromINput(input[0]);
			File f2=fromINput(input[1]);
			file2=f1;
			IDataSet dataSet = DataProjectAccess.getDataSet(f1);
			ds=dataSet.withPredictions(f2);
			setPartName(f1.getName()+"-"+f2.getName());
			init();
		}
	}

	private void init() {
		AnalisysEngine engine = new AnalisysEngine(ds);
		
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
		IFileEditorInput input=(IFileEditorInput) editorInput;
		IFile file = input.getFile();
		File file3 = file.getLocation().toFile();
		return file3;
	}
	
	@Override
	public void recalcView() {
		super.recalcView();
		DataProjectAccess.updateMeta(file2,ds);
	}



	@Override
	public void setFocus() {		
	}

	@Override
	protected File getActualTarget(String targetFile) {
		return new File(file2.getParentFile(),targetFile);
	}

}