package com.onpositive.dside.ui.editors.yaml.model;

public class PropertyDescription {

	protected boolean multivalue;
	public boolean isMultivalue() {
		return multivalue;
	}
	public void setMultivalue(boolean multivalue) {
		this.multivalue = multivalue;
	}
	protected Object defaultValue;
	protected boolean required;
	protected String type;
	protected String range;
	
	protected String[] fixedValues;
	protected Double min;
	protected Double max;
	protected Double step;
	public String customRealm;
	private boolean lowerCase;
	
	public boolean isLowerCase() {
		return lowerCase;
	}
	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}
	public String getCustomRealm() {
		return customRealm;
	}
	public void setCustomRealm(String customRealm) {
		this.customRealm = customRealm;
	}
	public Double getStep() {
		return step;
	}
	public void setStep(Double step) {
		this.step = step;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String[] getFixedValues() {
		return fixedValues;
	}
	public void setFixedValues(String[] fixedValues) {
		this.fixedValues = fixedValues;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
}
