package com.onpositive.dside.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;

import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.StandaloneDataSetGallery;

public class AugmentationOverviewEditorPart extends DeprecatableEditorPart {

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
	protected void createRegularContent(Composite parent) {
		Composite con = new Composite(parent, SWT.NONE);
		con.setLayout(new FillLayout());
		dataSetGallery = new StandaloneDataSetGallery(con, analizeResults);
	}

	@Override
	public void setFocus() {
		if (dataSetGallery != null) {
			dataSetGallery.getControl().setFocus();
		}
	}
}
