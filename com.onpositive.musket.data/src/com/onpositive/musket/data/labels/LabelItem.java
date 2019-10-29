package com.onpositive.musket.data.labels;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

public class LabelItem {

	@Required
	@Caption("Clazz")
	protected String clazz;
	
	public int getClazzNum() {
		try {
		return Integer.parseInt(clazz);
		}catch (Exception e) {
			return -1;
		}
	}
	
	@Caption("Label")
	protected String label;

}
