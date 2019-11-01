package com.onpositive.musket_core;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.onpositive.dside.tasks.PrivateServerTask;

public class ValidateTask extends PrivateServerTask<ValidationResult> {

	boolean debug;
	Experiment experiment;

	public ValidateTask(Experiment experiment) {
		super();
		this.experiment = experiment;
		this.path=experiment.getPath().toPortableString();
	}

	protected String path;

	@Override
	public Class<ValidationResult> resultClass() {
		return ValidationResult.class;
	}

	@Override
	public void afterCompletion(ValidationResult taskResult) {
		
	}

	@Override
	public boolean isDebug() {
		return false;
	}

	@Override
	public IProject[] getProjects() {
		ArrayList<org.eclipse.core.resources.IProject> p = new ArrayList<>();
		Experiment e = experiment;
		IPath path = e.getPath();
		IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		if (fileForLocation != null) {
			p.add(fileForLocation.getProject());
		}

		return p.toArray(new org.eclipse.core.resources.IProject[p.size()]);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
