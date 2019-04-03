package com.onpositive.dside.ui.navigator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ExperimentsFilter extends ViewerFilter {

	public ExperimentsFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IFolder) {
			IFolder el=(IFolder)element;
			if (el.getName().equals("experiments")) {
				return false;
			}
		}
		return true;
	}

}
