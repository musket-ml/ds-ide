package com.onpositive.dside.ui.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.views.ExperimentsView;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentFinder;

public class ShowExperimentsAction extends Action implements IObjectActionDelegate {

	private ISelection selection;
	private IWorkbenchPart part;

	@Override
	public void run(IAction action) {
		ArrayList<IContainer> flds = new ArrayList<>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection dl = (IStructuredSelection) selection;
			for (Object o : dl.toList()) {
				if (o instanceof IAdaptable) {
					IFolder adapter = ((IAdaptable) o).getAdapter(IFolder.class);
					if (adapter != null) {
						flds.add(adapter);
					}
				}
			}
		}
		
		try {
			ExperimentsView exp = (ExperimentsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("com.onpositive.dside.ui.experiments");
			Collection<Experiment> experiments = ExperimentFinder.find(flds);
			exp.setExperiments(experiments);
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
