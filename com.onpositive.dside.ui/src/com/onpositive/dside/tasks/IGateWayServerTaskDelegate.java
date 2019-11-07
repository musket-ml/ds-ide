package com.onpositive.dside.tasks;

public interface IGateWayServerTaskDelegate {

	public void started(GateWayRelatedTask task);
	public void terminated();
}
