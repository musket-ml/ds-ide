package com.onpositive.dside.ui.views;


import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.dside.ui.editors.ObjectEditorInput;
import com.onpositive.musket_core.DataSet;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.ui.workbench.elements.XMLView;

public class DataSetsView extends XMLView {

	private List<DataSet> datasets;

	public DataSetsView() {
		super("dlf/datasets.dlf");
	}

	public void open() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		Collection<Object> collection = ValueUtils.toCollection(currentValue);
		for (Object o : collection) {
			DataSet e = (DataSet) o;
			
			ObjectEditorInput er=new ObjectEditorInput(e);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
						er, "com.onpositive.dside.ui.dsviewer");
			} catch (PartInitException e1) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e1.getMessage());
			}
		}
	}	

	@Override
	public void setFocus() {
		
	}



	public void setDataSets(List<DataSet> datasets) {
		this.datasets=datasets;
		this.getBinding("datasets").refresh();		
	}

}
