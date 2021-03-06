package com.onpositive.musket_core;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import com.onpositive.dside.ui.IMusketConstants;

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
	
	public IProject getEclipseProject(Experiment experiment) {
		IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(experiment.getPath());
		if (fileForLocation!=null) {
			return fileForLocation.getProject();
		}
		return null;
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
	
	public static boolean looksMusketProject(IProject project) {
		return project.getFolder(IMusketConstants.MUSKET_EXPERIMENTS_FOLDER).exists() || 
			   project.getFile(IMusketConstants.COMMON_CONFIG_NAME).exists() ||
			   project.getFile(IMusketConstants.PROJECT_DEPS_FILE).exists();
	}
	
}
