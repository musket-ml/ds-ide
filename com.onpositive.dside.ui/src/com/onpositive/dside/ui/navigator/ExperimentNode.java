package com.onpositive.dside.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import com.onpositive.dside.ui.IMusketConstants;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentFinder;

public class ExperimentNode implements IAdaptable,IExperimentContribution{

	protected final IFolder folder;

	public ExperimentNode(IFolder folder) {
		super();
		this.folder = folder;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter==IFile.class||adapter==IResource.class) {
			return adapter.cast(folder.getFile(IMusketConstants.MUSKET_CONFIG_FILE_NAME));
		}
		if (adapter == Experiment.class) {
			return adapter.cast(getExperiment());
		}
		return null;
	}
	
	public String experimentName() {
		return folder.getName();
	}

	public IFolder getFolder() {
		return folder;
	}
	
	public Experiment getExperiment() {
		return ExperimentFinder.getExperiment(folder);
	}
}
