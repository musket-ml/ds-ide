package com.onpositive.dside.tasks;

import org.eclipse.debug.core.ILaunch;

public interface IServerTask<T> {

	Class<T> resultClass();
	
	public void afterCompletion(T taskResult);
	
	public boolean isDebug();
	
	org.eclipse.core.resources.IProject[] getProjects();
	
	public default void beforeStart() {};

	public default void afterStart(ILaunch launch) {};
	
	public String getPreferredLaunchConfigType();
	
	default boolean save() {return false;}
}
