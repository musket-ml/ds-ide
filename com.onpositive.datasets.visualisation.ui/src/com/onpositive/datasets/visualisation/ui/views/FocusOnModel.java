package com.onpositive.datasets.visualisation.ui.views;

import java.util.List;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.FixedBound;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/focusOn.dlf")
public class FocusOnModel {

	@Caption("Class")
	@RealmProvider(FocusOnRealmProvider.class)
	@FixedBound
	@Required
	String input;
	
	
	public List<String> classes;
	
	
}
