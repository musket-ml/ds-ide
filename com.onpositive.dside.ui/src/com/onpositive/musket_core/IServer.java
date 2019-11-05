package com.onpositive.musket_core;

public interface IServer {
	String hello();
	
	IMusketProject project(String path);
	
	Object performTask(String taskConfig, IProgressReporter reporter);
	
	String getDatasets(String search, Boolean mine);
	
	String getCompetitions(String search, Boolean mine);
	
	void downloadDataset(String id, String dest);
	
	void downloadCompetitionFiles(String id, String dest);
	
	void runOnKaggle(String projectPath);

	void downloadDeps(String fullPath);
}
