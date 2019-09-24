package com.onpositive.musket.data.core;

public interface IProgressMonitor {

	public boolean onBegin(String message,int totalTicks);
	public boolean onProgress(String message,int passsedTicks);
	public boolean onDone(String message,int totalTicks);
}
