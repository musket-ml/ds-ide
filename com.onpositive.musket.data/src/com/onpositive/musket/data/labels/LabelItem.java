package com.onpositive.musket.data.labels;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

public class LabelItem {

	@Required
	@Caption("Class")
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

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
