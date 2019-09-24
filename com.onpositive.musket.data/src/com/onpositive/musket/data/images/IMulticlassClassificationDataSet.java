package com.onpositive.musket.data.images;

import java.util.Collection;
import java.util.List;

import com.onpositive.musket.data.core.IDataSet;

public interface IMulticlassClassificationDataSet extends IDataSet{

	
	boolean isExclusive();
	
	Collection<? extends IMulticlassClassificationItem>items();
	
	
	List<String> classNames();
	
	IBinaryClassificationDataSet forClass(String clazz);
}
