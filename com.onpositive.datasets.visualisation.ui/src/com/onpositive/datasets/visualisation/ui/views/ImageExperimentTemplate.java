package com.onpositive.datasets.visualisation.ui.views;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;

public abstract class ImageExperimentTemplate extends GenericExperimentTemplate {

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
	
	
	
	public abstract String finish();
}