package com.onpositive.dside.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.ObjectEditorInput;
import com.onpositive.dside.ui.StandaloneDataSetGallery;

public class AugmentationOverviewEditorPart extends EditorPart {

	public static final String ID = "com.onpositive.dside.ui.editors.augmentation";
	private IAnalizeResults analizeResults;
	private StandaloneDataSetGallery dataSetGallery;

	@Override
	public void doSave(IProgressMonitor monitor) {
		//Do nothing - currently readonly
	}

	@Override
	public void doSaveAs() {
		//Do nothing
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}
	
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof ObjectEditorInput) {
			Object object = ((ObjectEditorInput) input).getObject();
			if (object instanceof IAnalizeResults) {
				analizeResults = (IAnalizeResults) object;
			}
		}
	}

	@Override
	public boolean isDirty() {
		// Always false
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		dataSetGallery = new StandaloneDataSetGallery(parent, analizeResults);
	}

	@Override
	public void setFocus() {
		dataSetGallery.getControl().setFocus();
	}

}
