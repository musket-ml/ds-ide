package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IDataSetFilter;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.text.AbstractTextDataSet;
import com.onpositive.musket.data.text.ITextDataSet;
import com.onpositive.musket.data.text.ITextItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Contains Text")
public class HasTextFilter implements IDataSetFilter<ITextDataSet>{

	protected String id;
	
	public HasTextFilter(String id) {
		super();
		this.id = id;
		if (id==null) {
			this.id="";
		}
	}

	@Override
	public boolean test(IItem arg0) {
		ITextItem it=(ITextItem) arg0;
		return it.getText().contains(id);
	}
}
