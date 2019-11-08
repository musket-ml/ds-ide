package com.onpositive.dside.ui.navigator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import com.onpositive.dside.ui.IMusketConstants;

public class ExperimentsNode implements IAdaptable,IHasExperiments{

	IFolder folder;

	public ExperimentsNode(IFolder folder) {
		super();
		this.folder = folder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((folder == null) ? 0 : folder.hashCode());
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
		if (folder == null) {
			if (other.folder != null)
				return false;
		} else if (!folder.equals(other.folder))
			return false;
		return true;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter==IFolder.class) {
			return adapter.cast(folder);
		}
		if (adapter==IContainer.class) {
			return adapter.cast(folder);
		}
		if (adapter==IResource.class) {
			return adapter.cast(folder);
		}
		return null;
	}
	
	public Object[] getChildren() {
		ArrayList<ExperimentNode>children=new ArrayList<>();
		try {
			folder.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getName().equals(".history")) {
						return false;
					}
					if (resource.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
						children.add(new ExperimentNode((IFolder) resource.getParent()));
					}
					// TODO Auto-generated method stub
					return true;
				}
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LinkedHashMap<IPath, ExperimentGroup>groups=new LinkedHashMap<>();
		for (ExperimentNode n:children) {
			IPath projectRelativePath = n.folder.getProjectRelativePath().removeFirstSegments(1).removeLastSegments(1);
			if (groups.containsKey(projectRelativePath)) {
				groups.get(projectRelativePath).experiments.add(n);
			}
			else {
				ExperimentGroup g=new ExperimentGroup(folder.getProject(),projectRelativePath);
				g.experiments.add(n);
				groups.put(projectRelativePath, g);				
			}
		}
		return groups.values().toArray();		
	}
}
