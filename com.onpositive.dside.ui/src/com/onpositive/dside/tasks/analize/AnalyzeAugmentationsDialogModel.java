package com.onpositive.dside.tasks.analize;

import com.onpositive.musket_core.Experiment;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/analyzeAugmentations.dlf")
public class AnalyzeAugmentationsDialogModel {
	
	protected Experiment experiment;
	protected boolean debug = false;

	@RealmProvider(expression = "experiment.DataSets")
	@Required
	protected String dataset = "train";

	public AnalyzeAugmentationsDialogModel(Experiment experiment) {
		super();
		this.experiment = experiment;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
