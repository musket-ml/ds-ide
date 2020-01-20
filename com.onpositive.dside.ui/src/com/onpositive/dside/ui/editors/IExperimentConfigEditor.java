package com.onpositive.dside.ui.editors;

import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ProjectWrapper;
import com.onpositive.yamledit.ast.Universe;

public interface IExperimentConfigEditor {

	ProjectWrapper getProject();
	
	Experiment getExperiment();
	
	Universe getRegistry();
}
