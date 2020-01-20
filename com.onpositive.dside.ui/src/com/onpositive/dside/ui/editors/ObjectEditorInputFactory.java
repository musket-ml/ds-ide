package com.onpositive.dside.ui.editors;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class ObjectEditorInputFactory implements IElementFactory {
	
	public static final String ID = "com.onpositive.dside.ui.editors.ObjectEditorInputFactory";

	@Override
	public IAdaptable createElement(IMemento memento) {
		return new ObjectEditorInput(null);
	}

}
