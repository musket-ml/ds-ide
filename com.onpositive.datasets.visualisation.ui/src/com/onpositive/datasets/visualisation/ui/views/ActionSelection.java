package com.onpositive.datasets.visualisation.ui.views;

import java.util.List;

import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Image;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/actionList.dlf")
@Image("generic_task")
class ActionSelection{

	@Required
	@Caption("Target file name")
	private String target;
	
	private List<ConversionAction> items;
	
	@Required("Please select converter")
	@Caption("Converter")
	
	private ConversionAction selection;

	public ActionSelection(List<ConversionAction> conversions) {
		this.items=conversions;
	}
	
	public String targetFile() {
		return target;
	}
	
	public ConversionAction getSelectedAction() {
		return selection;
	}
}