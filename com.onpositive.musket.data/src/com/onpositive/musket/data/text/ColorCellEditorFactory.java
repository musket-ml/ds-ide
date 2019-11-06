package com.onpositive.musket.data.text;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.ICellEditorFactory;

public class ColorCellEditorFactory implements ICellEditorFactory{

	@Override
	public CellEditor createEditor(Object arg0, Object arg1, Viewer arg2, IProperty arg3) {
		return new ColorCellEditor((Composite) arg2.getControl());
	}

}
