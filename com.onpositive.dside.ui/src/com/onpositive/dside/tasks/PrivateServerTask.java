package com.onpositive.dside.tasks;

public abstract class PrivateServerTask<T> implements IServerTask<T>{
	
	@Override
	public String getPreferredLaunchDelegate() {
		return "org.python.pydev.debug.musketLaunchConfigurationType";
	}

}
