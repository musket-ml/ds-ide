package com.onpositive.musket_core;


public interface IServer {

	String hello();
	
	IProject project(String path);
	
	String performTask(String taskConfig,IProgressReporter reporter);
		
}
