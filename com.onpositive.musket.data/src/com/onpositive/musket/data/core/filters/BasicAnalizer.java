package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("No Analisys")
public class BasicAnalizer extends AbstractAnalizer implements IAnalizer<GenericDataSet>{

	@Override
	protected Object group(IItem v) {
		return "";
	}

}
