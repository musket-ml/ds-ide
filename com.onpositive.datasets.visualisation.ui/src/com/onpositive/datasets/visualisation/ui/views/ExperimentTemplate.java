package com.onpositive.datasets.visualisation.ui.views;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;

public abstract class ExperimentTemplate {

	@Caption("Name")
	protected String name="newExperiment";
	
	@Caption("Width")
	protected  int width=224;
	@Caption("Height")
	protected  int height=224;
	
	@Caption("Architecture")
	protected String architecture="xception";
	
	@Caption("Horizontal Flip")
	protected boolean hFlip=true;
	
	
	@Caption("Vertical Flip")	
	protected boolean vFlip=true;
	
	protected boolean testTime=true;
	
	@Caption("Enable Test Time Augmentation")
	protected boolean enableTestTimeAugmentation=true;
	
	@Caption("Activation")
	protected String activation="sigmoid";
	
	@Caption("Classes")
	protected int numClasses=1;
	
	public abstract String finish();
	
	protected String loss="binary_crossentropy";
}
