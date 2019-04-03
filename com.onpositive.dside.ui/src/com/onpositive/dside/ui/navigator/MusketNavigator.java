package com.onpositive.dside.ui.navigator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.python.pydev.navigator.ui.PydevPackageExplorer;

public class MusketNavigator implements ITreeContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		if (parentElement instanceof IProject) {
			IProject pr=(IProject) parentElement;
			IFolder folder = pr.getFolder("experiments");
			if (folder.exists()) {
				return new Object[] {new ExperimentsNode(folder)};
			}
		}
		if (parentElement instanceof ExperimentsNode) {
			ExperimentsNode pm=(ExperimentsNode) parentElement;
			return pm.getChildren();
		}
		if (parentElement instanceof ExperimentGroup) {
			ExperimentGroup pm=(ExperimentGroup) parentElement;
			return pm.getChilden();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			return true;
		}
		if (element instanceof ExperimentsNode) {
			return true;
		}
		if (element instanceof ExperimentGroup) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

}
