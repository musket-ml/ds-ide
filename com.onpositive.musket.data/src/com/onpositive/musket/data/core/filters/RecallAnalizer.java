package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Multiclass Recall")
public class RecallAnalizer extends F1Analizer implements IAnalizer<IDataSetWithGroundTruth>{

	protected double func(String s) {
		return stat.get(s).recall();
	}
}
