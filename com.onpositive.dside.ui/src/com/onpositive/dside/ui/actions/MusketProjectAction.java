package com.onpositive.dside.ui.actions;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.onpositive.musket_core.ProjectManager;

public abstract class MusketProjectAction extends SelectedItemsAction {

	@Override
	protected boolean checkEnabled(List<?> selectedItems) {
		List<IProject> projects = getSelectedProjects(selectedItems);
		if (projects.size() == 1 && ProjectManager.looksMusketProject(projects.get(0))) {
			return true;
		}
		return false;
	}
	
	protected IProject getFirstSelectedMusketProject(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			List<IProject> selectedProjects = getSelectedProjects(((IStructuredSelection) selection).toList());
			for (IProject project : selectedProjects) {
				if (ProjectManager.looksMusketProject(project)) {
					return project;
				}
			}
		}
		return null;
	}
	
	protected List<IProject> getSelectedProjects(List<?> selectedItems) {
		return selectedItems.stream().map(item -> getResource(item)).filter(res -> res != null).map(res -> res.getProject()).distinct().collect(Collectors.toList());
	}

	private IResource getResource(Object item) {
		if (item instanceof IResource) {
			return (IResource) item;
		}
		if (item instanceof IAdaptable) {
			return ((IAdaptable) item).getAdapter(IResource.class);
		}
		return null;
	}

}
