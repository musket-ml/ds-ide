package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetFilter;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Filter by item id")
public class IdFilter implements IDataSetFilter<IDataSet>{

	protected String id;
	
	public IdFilter(String id) {
		super();
		this.id = id;
		if (id==null) {
			this.id="";
		}
	}

	@Override
	public boolean test(IItem t) {
		return t.id().contains(id);
	}

}
