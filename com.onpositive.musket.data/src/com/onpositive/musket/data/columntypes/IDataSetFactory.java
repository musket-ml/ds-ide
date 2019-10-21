package com.onpositive.musket.data.columntypes;

import java.util.Map;

import com.onpositive.musket.data.core.IDataSet;

public interface IDataSetFactory {

	String caption();
	
	double estimate(DataSetSpec parameterObject);

	IDataSet create(DataSetSpec spec,Map<String,Object>options);

}
