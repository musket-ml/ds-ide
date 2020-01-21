package com.onpositive.dside.ui.actions;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public abstract class SelectedItemsAction extends Action implements IObjectActionDelegate {

	protected IWorkbenchPart activePart;
	protected ISelection selection;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		setEnabled(checkEnabled(getSelectedItems()));
	}
	
	protected List<?> getSelectedItems() {
		if (selection instanceof IStructuredSelection) {
			return ((IStructuredSelection) selection).toList();
		}
		return Collections.emptyList();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart activePart) {
		this.activePart = activePart;
	}
	
	protected abstract boolean checkEnabled(List<?> selectedItems);
	
}
