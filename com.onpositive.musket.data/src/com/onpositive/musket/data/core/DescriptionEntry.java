package com.onpositive.musket.data.core;

import java.text.NumberFormat;


public class DescriptionEntry {

	private String caption;
	
	private Object value;
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public DescriptionEntry(String caption, Object value) {
		super();
		this.caption = caption;
		this.value = value;
	}
	
	public DescriptionEntry() {
		
	}
	
	public String getStringValue() {
		if (value instanceof Number) {
			return NumberFormat.getInstance().format(value);
		}
		return value.toString();
	}
	
	
}
