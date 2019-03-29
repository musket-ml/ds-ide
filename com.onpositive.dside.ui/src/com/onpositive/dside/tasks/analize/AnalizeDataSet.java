package com.onpositive.dside.tasks.analize;

import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.IProject;
import com.onpositive.musket_core.IServer;

public class AnalizeDataSet implements IGateWayServerTaskDelegate{

	protected Experiment experiment;
	protected String dataset;
	
	@Override
	public void started(IServer server, IProject project) {
		
	}

	@Override
	public void terminated() {
		
	}

}
