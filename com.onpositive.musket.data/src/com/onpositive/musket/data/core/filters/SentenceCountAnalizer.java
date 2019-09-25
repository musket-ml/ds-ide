package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.text.AbstractTextDataSet;
import com.onpositive.musket.data.text.ITextItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Group by sentence count")
@ProvidesFilter("sentence count")
public class SentenceCountAnalizer extends AbstractAnalizer implements IAnalizer<AbstractTextDataSet>{

	@Override
	protected Object group(IItem v) {
		ITextItem ti=(ITextItem) v;
		String text = ti.getText();
		return SimpleSentenceSplitter.getInstance().split(text).length;
	}

}
