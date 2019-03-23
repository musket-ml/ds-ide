package com.onpositive.musket_core;

public interface IProgressReporter {

	boolean isCanceled();
	
	void task(String name,int totalWork);
	
	boolean worked(int worked);
	
	void done();
}
