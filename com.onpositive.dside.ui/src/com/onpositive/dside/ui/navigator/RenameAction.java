package com.onpositive.dside.ui.navigator;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.actions.RenameResourceAction;
import org.eclipse.ui.navigator.CommonNavigator;

public class RenameAction implements IObjectActionDelegate{

	public RenameAction() {
	}

	private ISelection selection;
	private IWorkbenchPart part;

	
	@Override
	public void run(IAction action) {
		RenameResourceAction deleteResourceAction = new RenameResourceAction(part.getSite().getWorkbenchWindow());
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ddl=(IStructuredSelection) selection;
			ArrayList<Object>newSelection=new ArrayList<>();
			for (Object o:ddl.toArray()) {
				if (o instanceof ExperimentGroup) {
					ExperimentGroup g=(ExperimentGroup) o;
					if (g.getPath().isEmpty()) {
						continue;
					}
				}
				if (o instanceof ExperimentNode) {
					newSelection.add(((ExperimentNode)o).folder);
				}
				else {
					newSelection.add(o);
				}
			}
			deleteResourceAction.selectionChanged(new StructuredSelection(newSelection));
			deleteResourceAction.run();
		}
		CommonNavigator activePart = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		activePart.getCommonViewer().refresh();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection=selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part=targetPart;
	}
}
