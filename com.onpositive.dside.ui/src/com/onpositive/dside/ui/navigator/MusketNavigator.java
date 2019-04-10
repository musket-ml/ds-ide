package com.onpositive.dside.ui.navigator;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
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
		if (parentElement instanceof ExperimentNode) {
			ExperimentNode pm=(ExperimentNode) parentElement;
			ArrayList<IResource>res=new ArrayList<>();
			try {
				pm.folder.accept(new IResourceVisitor() {
					
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource.getName().equals("config.yaml")) {
							return false;
						}
						if (resource.equals(pm.folder)) {
							return true;
						}
						res.add(resource);
						return false;
					}
				}, IResource.DEPTH_ONE, false);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res.toArray();
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
		Object[] children = getChildren(element);
		return children!=null&&children.length>0;		
	}

}
