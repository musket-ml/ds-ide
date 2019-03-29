package com.onpositive.musket_core;

import com.onpositive.dside.dto.PythonError;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/error.dlf")
public class StackVisualizer {

	public StackVisualizer(PythonError pythonError) {
		this.error=pythonError;
	}

	PythonError error;
}