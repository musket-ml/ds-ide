package com.onpositive.dside.ui.navigator;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import static com.onpositive.dside.ui.IMusketConstants.*;

public class MusketNavigatorContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		return this.getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			IProject project = (IProject) parentElement;
			IFolder folder = project.getFolder(MUSKET_EXPERIMENTS_FOLDER);
			if (folder.exists()) {
				return new Object[] { new ExperimentsNode(project) };
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
						if (resource.getName().equals(MUSKET_CONFIG_FILE_NAME)) {
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
		if (element instanceof ExperimentNode) {
			ExperimentNode node = (ExperimentNode) element;
			IFolder folder = node.getFolder();
			if (folder.getParent().getName().equals(MUSKET_EXPERIMENTS_FOLDER)) {
				return new ExperimentsNode(folder.getProject());			
			} else {
				return new ExperimentGroup(folder.getProject(), folder.getParent().getProjectRelativePath().removeFirstSegments(1));
			}
		}
		if (element instanceof ExperimentsNode) {
			ExperimentsNode experimentsNode = (ExperimentsNode) element;
			return experimentsNode.getProject();
		}
		if (element instanceof ExperimentGroup) {
			ExperimentGroup group = (ExperimentGroup) element;
			IProject project = group.getProject();
			IPath path = group.getPath();
			if (path.segmentCount() > 1) {
				return new ExperimentGroup(project, path.uptoSegment(path.segmentCount() - 1));
			} else {
				return new ExperimentsNode(project);
			}
		}
		if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			if (folder.getName().equals(MUSKET_EXPERIMENTS_FOLDER)) {
				return folder.getProject();			
			}
			if (folder.getFile(MUSKET_CONFIG_FILE_NAME).exists()) {
				if (folder.getParent().getName().equals(MUSKET_EXPERIMENTS_FOLDER)) {
					return new ExperimentsNode(folder.getProject());			
				} else {
					return new ExperimentGroup(folder.getProject(), folder.getParent().getProjectRelativePath());
				}
			}
		}
		if (element instanceof IFile && ((IResource) element).getName().equals(MUSKET_CONFIG_FILE_NAME)) {
			return getParent(((IResource) element).getParent());
//			return new ExperimentNode((IFolder) ((IResource) element).getParent());			
		}
		if (element instanceof IResource) {
			return ((IResource) element).getParent();
		}
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
