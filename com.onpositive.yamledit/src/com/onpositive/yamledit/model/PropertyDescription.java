package com.onpositive.yamledit.model;

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
	protected String description;
	protected String items;
	protected boolean reference;
	protected boolean autoConvertToArray;
	private boolean lowerCase;
	private String customValidator;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public boolean isReference() {
		return reference;
	}
	public void setReference(boolean reference) {
		this.reference = reference;
	}
	public boolean isAutoConvertToArray() {
		return autoConvertToArray;
	}
	public void setAutoConvertToArray(boolean autoConvertToArray) {
		this.autoConvertToArray = autoConvertToArray;
	}
	public String getCustomValidator() {
		return customValidator;
	}
	public void setCustomValidator(String customValidator) {
		this.customValidator = customValidator;
	}
}
