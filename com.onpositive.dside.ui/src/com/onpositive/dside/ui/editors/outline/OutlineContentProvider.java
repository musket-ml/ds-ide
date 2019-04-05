package com.onpositive.dside.ui.editors.outline;

import org.aml.typesystem.beans.IPropertyView;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.onpositive.dside.ast.ASTElement;

public class OutlineContentProvider implements ITreeContentProvider {

	private OutlineNode node;

	@Override
	public Object[] getElements(Object inputElement) {
		ASTElement element = (ASTElement) inputElement;
		IPropertyView propertiesView = element.getType().toPropertiesView();
		node = new OutlineNode(element, null);
		
		return node.getChildren().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		OutlineNode node = (OutlineNode) parentElement;
		return node.getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {
		OutlineNode node = (OutlineNode) element;
		return node.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public OutlineNode tryToFindByOffset(int caretOffset) {
		return node;
	}

}
