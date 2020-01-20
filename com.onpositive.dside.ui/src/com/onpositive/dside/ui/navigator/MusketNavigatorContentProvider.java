package com.onpositive.dside.ui.navigator;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.IMusketConstants;

public class MusketNavigatorContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			IProject project = (IProject) parentElement;
			IFolder folder = project.getFolder(IMusketConstants.MUSKET_EXPERIMENTS_FOLDER);
			if (folder.exists()) {
				return new Object[] { new ExperimentsNode(folder) };
			}
		}
		if (parentElement instanceof ExperimentsNode) {
			ExperimentsNode experimentsNode = (ExperimentsNode) parentElement;
			return experimentsNode.getChildren();
		}
		if (parentElement instanceof ExperimentGroup) {
			ExperimentGroup experimentGroup = (ExperimentGroup) parentElement;
			return experimentGroup.getChilden();
		}
		if (parentElement instanceof ExperimentNode) {
			ExperimentNode pm = (ExperimentNode) parentElement;
			ArrayList<IResource> res = new ArrayList<>();
			try {
				pm.folder.accept(new IResourceVisitor() {

					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
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
				DSIDEUIPlugin.log(e);
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
