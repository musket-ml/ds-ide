package com.onpositive.musket_core;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;

public class ProjectManager {
	
	private static ProjectManager instance=new ProjectManager();

	protected HashMap<String, ProjectWrapper>projects=new HashMap<>();
	
	public final static ProjectManager getInstance() {
		return instance;
	}
	
	public ProjectWrapper getProject(Experiment experiment) {
		String projectPath = experiment.getProjectPath();
		return getProject(projectPath);		
	}

	public ProjectWrapper getProject(String projectPath) {
		if (projectPath==null) {
			return new ProjectWrapper(null);
		}
		ProjectWrapper projectWrapper = projects.get(projectPath);
		if (projectWrapper!=null) {
			return projectWrapper;
		}
		projectWrapper=new ProjectWrapper(projectPath);
		projects.put(projectPath, projectWrapper);
		return projectWrapper;
	}

	public static ProjectWrapper getInstance(IProject project) {
		return getInstance().getProject(project.getLocation().toOSString());
	}	
}
