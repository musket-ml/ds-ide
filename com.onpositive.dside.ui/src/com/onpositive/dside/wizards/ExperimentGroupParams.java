package com.onpositive.dside.wizards;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/experimentGroupInit.dlf")
public class ExperimentGroupParams{

	@Caption("Project")
	@Required
	String project;
	
	@Caption("Experiment group")
	@Required
	String group;
	
	
}