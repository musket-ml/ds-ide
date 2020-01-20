package com.onpositive.musket_core;

import org.eclipse.core.resources.IProject;

import com.onpositive.dside.tasks.PrivateServerTask;

public class ExportForWeb extends PrivateServerTask<ValidationResult> {

	boolean debug=true;
	String experiment;
	String resultPath;
	public String getResultPath() {
		return resultPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	IProject project;

	public ExportForWeb(IProject project,String experiment,IProject target) {
		super();
		this.project=project;
		this.path=experiment;
		this.resultPath=target.getLocation().toPortableString();
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
		return true;
	}

	@Override
	public IProject[] getProjects() {
		return new IProject[] {project};
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
