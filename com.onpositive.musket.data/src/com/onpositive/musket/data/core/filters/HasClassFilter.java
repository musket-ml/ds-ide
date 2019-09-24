package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IDataSetFilter;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Has Class")
public class HasClassFilter implements IDataSetFilter<IMulticlassClassificationDataSet>{

	protected String id;
	
	public HasClassFilter(String id) {
		super();
		this.id = id;
		if (id==null) {
			this.id="";
		}
	}

	@Override
	public boolean test(IItem arg0) {
		IMulticlassClassificationItem it=(IMulticlassClassificationItem) arg0;
		return it.classes().contains(id);
	}
}
