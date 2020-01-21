package com.onpositive.dside.ui.actions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.python.pydev.shared_ui.utils.UIUtils;

import com.onpositive.dside.ui.DSIDEUIPlugin;

public class DuplicateExperimentAction implements IObjectActionDelegate {

	private ISelection selection;
	private IWorkbenchPart targetPart;

	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IStructuredSelection sel  = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			if (element instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) element;
				IFile file = adaptable.getAdapter(IFile.class);
				if (file.getName().endsWith(".yaml")) {
					IFolder folder = (IFolder) file.getParent();
					String newName = chooseName(folder.getParent(),folder.getName());
					InputDialog inputDialog = new InputDialog(UIUtils.getActiveShell(), "New experiment name", "Enter new experiment name", newName, val -> {
						if (folder.getParent().exists(new Path(val))) {
							return "Experiment ore resource '" + val + "' already exists.";
						}	
						return null;
					});
					if (inputDialog.open() == Window.OK) {
						String selectedName = inputDialog.getValue();
						new Job("Copy experiment") {
							
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									ResourcesPlugin.getWorkspace().run(monitor1 -> {
										IFolder newFolder = folder.getParent().getFolder(new Path(selectedName));
										newFolder.create(true, true, monitor1);
										file.copy(newFolder.getFullPath().append(file.getName()),true, monitor1);
										folder.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor1);
									}, monitor);
									if (targetPart instanceof CommonNavigator) {
										Display.getDefault().asyncExec(() -> {
											CommonNavigator navigator = (CommonNavigator) targetPart;
											navigator.getCommonViewer().refresh(folder.getProject());
										});
									}
								} catch (CoreException e) {
									return new Status(Status.ERROR, DSIDEUIPlugin.PLUGIN_ID,"Error copying experiment", e);
								}
								return Status.OK_STATUS;
							}
							
						}.schedule();
					}
				}
			}
		}
	}

	protected String chooseName(IContainer parent, String name) {
		int i = 1;
		String newName = name + i;
		while(parent.exists(new Path(newName))) {
			newName = name + ++i;
		}
		return newName;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

}
