package com.onpositive.dside.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.musket_core.Experiment;

public class AddExperimentsToViewAction extends Action implements IObjectActionDelegate {

	private ISelection selection;
	private IWorkbenchPart part;

	@Override
	public void run(IAction action) {
		ArrayList<Experiment> toAdd = new ArrayList<>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Object current : structuredSelection.toList()) {
				if (current instanceof Experiment) {
					toAdd.add((Experiment) current);
				} else if (current instanceof IAdaptable) {
					Experiment adapter = ((IAdaptable) current).getAdapter(Experiment.class);
					if (adapter != null) {
						toAdd.add(adapter);
					}
				}
			}
		}
		
		try {
			ExperimentsView exp = (ExperimentsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("com.onpositive.dside.ui.experiments");
			exp.addExperiments(toAdd);
		} catch (PartInitException e) {
			DSIDEUIPlugin.log(e);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection != null) {
			this.selection = selection;
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (part != null) {
			this.part = targetPart;
		}
	}
}
