package com.onpositive.musket_core;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/errors.dlf")
public class Errors {

	protected ArrayList<ExperimentError>errors=new ArrayList<>();

	public ArrayList<ExperimentError> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<ExperimentError> errors) {
		this.errors = errors;
	}
}
