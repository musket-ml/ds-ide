package com.onpositive.dside.ui.editors;

import org.eclipse.ui.editors.text.TextEditor;

import com.onpositive.dside.ui.editors.yaml.model.ModelNode;
import com.onpositive.dside.ui.editors.yaml.model.NodeKind;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

import de.jcup.yamleditor.YamlEditor;

@Display("dlf/experiment_editor.dlf")
public class EditorModel {

	
	private ModelNode root;
	

	public ModelNode getRoot() {
		return root;
	}

	public void setRoot(ModelNode root) {
		this.root = root;
	}

	public EditorModel(TextEditor experiment,NodeKind kind) {
		YamlEditor ed=(YamlEditor) experiment;
		this.root=new ModelNode(ed.getDocument().get(),kind);
	}

	public void update(String string) {
		this.root.update(string);
	}
}
