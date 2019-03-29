package com.onpositive.dside.tasks;

import com.onpositive.musket_core.IProject;
import com.onpositive.musket_core.IServer;

public interface IGateWayServerTaskDelegate {

	public void started(IServer server,IProject project);
	public void terminated();
}
