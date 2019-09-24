package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.text.AbstractTextDataSet;
import com.onpositive.musket.data.text.ITextItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Group by text length")
@ProvidesFilter("text length")
public class TextLengthAnalizer extends AbstractAnalizer implements IAnalizer<AbstractTextDataSet>{

	@Override
	protected Object group(IItem v) {
		ITextItem ti=(ITextItem) v;
		String text = ti.getText();
		return text.length();
	}

}
