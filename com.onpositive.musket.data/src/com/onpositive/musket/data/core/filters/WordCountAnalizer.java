package com.onpositive.musket.data.core.filters;

import java.util.function.Function;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.text.AbstractTextDataSet;
import com.onpositive.musket.data.text.ITextDataSet;
import com.onpositive.musket.data.text.ITextItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Group by word count")
@ProvidesFilter("word count")
public class WordCountAnalizer extends AbstractAnalizer implements IAnalizer<ITextDataSet>,IColumnDependentAnalizer{

	@Override
	protected Object group(IItem v) {
		ITextItem ti=(ITextItem) v;
		String text = ti.getText();
		return get(text);
	}

	protected int get(String text) {
		return new BreakIteratorTokenizer().split(text).length;
	}
	@Override
	public String getName(IColumn c) {
		return "Group by "+c.caption()+" text length";
	}
	

}
