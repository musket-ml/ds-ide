package com.onpositive.musket.data.core.filters;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Multiclass Precision")
public class PrecisionAnalizer extends F1Analizer{

	protected double func(String s) {
		return stat.get(s).precision();
	}
}
