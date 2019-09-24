package com.onpositive.musket.data.core;


public interface IDataSetDelta {

	IDataSet additions();	
	IDataSet removals();	
	IDataSet changes();
	default boolean isEmpty() {
		return additions().isEmpty()&&removals().isEmpty()&&changes().isEmpty();
	}

}
