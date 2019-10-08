package com.onpositive.dside.tasks;

import com.onpositive.musket_core.IMusketProject;
import com.onpositive.musket_core.IServer;

public interface IGateWayServerTaskDelegate {

	public void started(GateWayRelatedTask task);
	public void terminated();
}
