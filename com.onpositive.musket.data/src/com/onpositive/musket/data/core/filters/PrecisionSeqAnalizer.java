package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.text.TextSequenceDataSetWithPredictions;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Multiclass Precision")
public class PrecisionSeqAnalizer extends F1SeqAnalizer implements IAnalizer<TextSequenceDataSetWithPredictions>{

	protected double func(String s) {
		return stat.get(s).precision();
	}
	
	protected String getYName() {
		return "Precision";
	}
}
