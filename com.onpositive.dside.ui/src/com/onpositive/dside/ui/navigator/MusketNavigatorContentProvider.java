package com.onpositive.dside.ui.navigator;

import static com.onpositive.dside.ui.IMusketConstants.MUSKET_CONFIG_FILE_NAME;
import static com.onpositive.dside.ui.IMusketConstants.MUSKET_EXPERIMENTS_FOLDER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;

import com.onpositive.dside.ui.DSIDEUIPlugin;

public class MusketNavigatorContentProvider implements ITreeContentProvider{

	private Viewer viewer;
	
	private IResourceChangeListener resListener = new IResourceChangeListener() {
		
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				Set<IFolder> toUpdate = new HashSet<IFolder>();
				event.getDelta().accept(delta -> {
					if (delta.getResource() instanceof IFolder) {
						if (isExperimentFolder((IFolder) delta.getResource()) && (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.REMOVED)) {
							toUpdate.add(delta.getResource().getProject().getFolder(MUSKET_EXPERIMENTS_FOLDER));
							return false;
						} else {
							return true;
						}
					}
					return delta.getResource() instanceof IContainer;
				});
				if (!toUpdate.isEmpty()) {
					updateFolders(toUpdate);
				}
			} catch (CoreException e) {
				DSIDEUIPlugin.log(e);
			}
		}
	};

	public MusketNavigatorContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resListener);
	}
	
	protected void updateFolders(Set<IFolder> toUpdate) {
		List<ExperimentsNode> nodes = toUpdate.stream().map(folder -> new ExperimentsNode(folder.getProject())).distinct().collect(Collectors.toList());
		Display.getDefault().asyncExec(() -> {
			if (viewer instanceof CommonViewer) {
				for (ExperimentsNode experimentsNode : nodes) {
					((CommonViewer) viewer).refresh(experimentsNode, true);
				}
			}
		});
		
	}

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

	private boolean isExperimentFolder(IFolder folder) {
		return folder.getFile(MUSKET_CONFIG_FILE_NAME).exists(); 
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;	
	}
	
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resListener);
	}

}
