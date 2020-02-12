package com.onpositive.dside.ui.navigator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.onpositive.dside.ui.IMusketConstants;

public class ExperimentsFilter extends ViewerFilter {

	public ExperimentsFilter() {
		// Default constructor
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IFolder) {
			IFolder el=(IFolder)element;
			if (el.getName().equals(IMusketConstants.MUSKET_EXPERIMENTS_FOLDER) && el.getParent() instanceof IProject) {
				return false;
			}
		}
		return true;
	}

}
