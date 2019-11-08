package com.onpositive.dside.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import com.onpositive.dside.ui.IMusketConstants;

public class ExperimentNode implements IAdaptable,IExperimentContribution{

	protected IFolder folder;

	public ExperimentNode(IFolder folder) {
		super();
		this.folder = folder;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		if (adapter==IFile.class||adapter==IResource.class) {
			return adapter.cast(folder.getFile(IMusketConstants.MUSKET_CONFIG_FILE_NAME));
		}
		return null;
	}
	
	public String experimentName() {
		return folder.getName();
	}
}
