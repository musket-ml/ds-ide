package com.onpositive.dside.ui.navigator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.IMusketConstants;
import static com.onpositive.dside.ui.IMusketConstants.*;

public class ExperimentsNode implements IAdaptable,IHasExperiments{


	private IProject project;

	public ExperimentsNode(IProject project) {
		super();
		this.project = project;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExperimentsNode other = (ExperimentsNode) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == getClass()) {
			return adapter.cast(this);
		}
		if (adapter==IFolder.class) {
			return adapter.cast(getExperimentsFolder());
		}
		if (adapter==IContainer.class) {
			return adapter.cast(getExperimentsFolder());
		}
		if (adapter==IResource.class) {
			return adapter.cast(getExperimentsFolder());
		}
		return null;
	}

	public IFolder getExperimentsFolder() {
		return project.getFolder(MUSKET_EXPERIMENTS_FOLDER);
	}
	
	public Object[] getChildren() {
		List<ExperimentNode> children = getExperiments();
		LinkedHashMap<IPath, ExperimentGroup>groups=new LinkedHashMap<>();
		for (ExperimentNode n:children) {
			IPath projectRelativePath = n.folder.getProjectRelativePath().removeFirstSegments(1).removeLastSegments(1);
			if (groups.containsKey(projectRelativePath)) {
				groups.get(projectRelativePath).experiments.add(n);
			}
			else {
				ExperimentGroup g=new ExperimentGroup(project,projectRelativePath);
				g.experiments.add(n);
				groups.put(projectRelativePath, g);				
			}
		}
		if (groups.values().size() == 1) {
			return children.toArray();
		}
		return groups.values().toArray();		
	}

	public List<ExperimentNode> getExperiments() {
		ArrayList<ExperimentNode>children=new ArrayList<>();
		try {
			getExperimentsFolder().accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getName().equals(".history")) {
						return false;
					}
					if (resource.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
						children.add(new ExperimentNode((IFolder) resource.getParent()));
					}
					return true;
				}
			});
		} catch (CoreException e) {
			DSIDEUIPlugin.log(e);
		}
		return children;
	}

	public Object getProject() {
		return project;
	}

}
