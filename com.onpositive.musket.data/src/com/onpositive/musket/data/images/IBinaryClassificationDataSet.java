package com.onpositive.musket.data.images;

import java.util.Collection;

import com.onpositive.musket.data.core.IDataSet;

public interface IBinaryClassificationDataSet extends IDataSet{

	
	
	public Collection<? extends IBinaryClasificationItem> items();
	
}
