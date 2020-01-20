package com.onpositive.dside.ui.views;

import org.eclipse.jface.action.Action;

public class RemoveSelectedExperimentAction extends Action {

	private ExperimentsView experimentsView;

	public RemoveSelectedExperimentAction(ExperimentsView experimentsView) {
		this.experimentsView = experimentsView;
	}
	
	@Override
	public void run() {
		experimentsView.removeSelectedExperiment();
	}
	

}
