package com.onpositive.musket_core;

import com.onpositive.dside.tasks.PrivateServerTask;
import com.onpositive.yamledit.introspection.InstrospectionResult;

public class IntrospectTask extends PrivateServerTask<InstrospectionResult>{

	protected String path;
	protected transient ProjectWrapper wrapper;

	public IntrospectTask(ProjectWrapper wrapper) {
		super();
		this.path =wrapper.path;
		this.wrapper=wrapper;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void afterCompletion(InstrospectionResult taskResult) {
		wrapper.refreshed(taskResult);
	}
	@Override
	public Class<InstrospectionResult> resultClass() {
		return InstrospectionResult.class;
	}

	@Override
	public boolean isDebug() {
		return false;
	}

	@Override
	public org.eclipse.core.resources.IProject[] getProjects() {
		return new org.eclipse.core.resources.IProject[0];
	}
}
