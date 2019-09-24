package com.onpositive.musket.data.core;

import java.util.Map;
import java.util.function.Predicate;

public interface IFilterProto extends IProto{


	Predicate<IItem> apply(IDataSet original,Map<String,Object>parameters);
	
}
