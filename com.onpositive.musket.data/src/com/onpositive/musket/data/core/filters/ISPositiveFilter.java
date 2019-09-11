package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IDataSetFilter;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IBinaryClasificationItem;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Accept positive items")
public class ISPositiveFilter implements IDataSetFilter<IBinaryClassificationDataSet>{

	public ISPositiveFilter() {
	}
	
	@Override
	public boolean test(IItem t) {
		IBinaryClasificationItem it=(IBinaryClasificationItem) t;
		return it.isPositive();
	}

}
