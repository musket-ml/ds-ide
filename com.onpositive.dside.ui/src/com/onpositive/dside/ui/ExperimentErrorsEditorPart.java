package com.onpositive.dside.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.musket_core.Errors;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class ExperimentErrorsEditorPart extends EditorPart {

	
	private Errors errors;

	public ExperimentErrorsEditorPart(Errors e) {
		this.errors=e;
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		RootElement rl=new RootElement(parent);
		IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(errors,null,null);
		System.out.println(widgetObject);
		IUIElement<?> createWidget = widgetObject.createWidget(new Binding(errors));
		rl.add((AbstractUIElement<?>) createWidget);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
	}

	@Override
	public void doSaveAs() {
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setInput(input);
		super.setSite(site);		
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
