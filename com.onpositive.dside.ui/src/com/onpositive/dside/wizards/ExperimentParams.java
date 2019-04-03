package com.onpositive.dside.wizards;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/experimentInit.dlf")
public class ExperimentParams{

	@Caption("Project")
	@Required
	String project;
	
	@Caption("Experiment group")
	String group;
	
	@Required
	@Caption("Experiment name")
	String name;
	
	String description;
}