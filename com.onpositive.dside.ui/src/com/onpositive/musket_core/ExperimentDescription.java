package com.onpositive.musket_core;

import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;

@TextLabel(provider=ExperimentDescriptionLabelProvider.class)
public class ExperimentDescription {

	protected String description;
	protected Experiment experiment;
	
	public ExperimentDescription(String description, Experiment experiment) {
		super();
		this.description = description;
		this.experiment = experiment;
	}
}
