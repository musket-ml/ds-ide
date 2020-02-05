package com.onpositive.dside.ui.navigator;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.onpositive.dside.ui.IMusketConstants;

public class ExperimentGroup implements IAdaptable,IExperimentContribution{

	protected ArrayList<ExperimentNode>experiments=new ArrayList<>();
	private  IPath path;
	private IProject project;
	
	
	public ExperimentGroup(IProject project,IPath experimentRootRelativePath) {
		this.setPath(experimentRootRelativePath);
		this.project=project;
	}

	public Object[] getChilden() {
		return experiments.toArray();
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter==IFolder.class) {
			return adapter.cast(project.getFolder(new Path(IMusketConstants.MUSKET_EXPERIMENTS_FOLDER).append(this.getPath())));
		}
		if (adapter==IContainer.class) {
			return adapter.cast(project.getFolder(new Path(IMusketConstants.MUSKET_EXPERIMENTS_FOLDER).append(this.getPath())));
		}
		if (adapter==IResource.class) {
			return adapter.cast(project.getFolder(new Path(IMusketConstants.MUSKET_EXPERIMENTS_FOLDER).append(this.getPath())));
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
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
		ExperimentGroup other = (ExperimentGroup) obj;
		if (getPath() == null) {
			if (other.getPath() != null)
				return false;
		} else if (!getPath().equals(other.getPath()))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	public IPath getPath() {
		return path;
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public IProject getProject() {
		return project;
	}
}
