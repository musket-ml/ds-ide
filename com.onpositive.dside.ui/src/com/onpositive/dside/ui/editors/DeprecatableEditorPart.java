package com.onpositive.dside.ui.editors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class DeprecatableEditorPart extends EditorPart {
	

	@Override
	public void createPartControl(Composite parent) {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof ObjectEditorInput && ((ObjectEditorInput) editorInput).getObject() == null) {
			createDeprecatedContent(parent);
		} else {
			createRegularContent(parent);
		}
	}
	
	protected abstract void createRegularContent(Composite parent);

	protected void createDeprecatedContent(Composite parent) {
		Composite con = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(con);
		Label deprectedLabel = new Label(con, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(deprectedLabel);
		deprectedLabel.setText("This editor's content is deprecated and can't be shown. Please reopen it if necessary.");
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

}
