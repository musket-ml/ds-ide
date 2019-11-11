package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.text.DocumentWithPredictions;
import com.onpositive.musket.data.text.TextSequenceDataSetWithPredictions;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Binary confusion matrics")
public class SequenceMatchAnalizer extends AbstractAnalizer implements IAnalizer<TextSequenceDataSetWithPredictions>{

	@Override
	protected Object group(IItem v) {
		DocumentWithPredictions ps=(DocumentWithPredictions) v;
		if (ps.allMatches()) {
			return "All Matches";
		}
		else {
			return "Some differences";
		}
	}

}
