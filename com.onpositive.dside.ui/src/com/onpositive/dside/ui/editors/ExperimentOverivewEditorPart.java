package com.onpositive.dside.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.EditorPart;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.dside.ui.editors.yaml.model.INodeListener;
import com.onpositive.dside.ui.editors.yaml.model.ModelNode;
import com.onpositive.dside.ui.editors.yaml.model.NodeKind;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

import de.jcup.yamleditor.YamlEditor;

public class ExperimentOverivewEditorPart extends EditorPart implements INodeListener{


	private TextEditor experiment;
	private EditorModel model;
	private Binding binding;

	public ExperimentOverivewEditorPart(TextEditor editor) {
		this.experiment=editor;
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		RootElement rl=new RootElement(parent);
		NodeKind kind = new NodeKind("basicConfig",this);
		model = new EditorModel(experiment,kind);
		IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(model,null,null);
		binding = new Binding(model);
		IUIElement<?> createWidget = widgetObject.createWidget(binding);
		rl.add((AbstractUIElement<?>) createWidget);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		this.dirty=false;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setInput(input);
		super.setSite(site);		
	}
	boolean dirty;

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void updateFromText() {
		model.update(((YamlEditor)experiment).getDocument().get());
		binding.refresh(true);
		
	}

	public void updateText() {
		if (dirty) {
			String string = model.getRoot().toString();
			((YamlEditor)experiment).getDocument().set(string);
			this.dirty=false;
			firePropertyChange(PROP_DIRTY);
		}
	}

	@Override
	public void updated(ModelNode node, Object newValue, String property) {
		this.dirty=true;
		firePropertyChange(PROP_DIRTY);
	}


}
