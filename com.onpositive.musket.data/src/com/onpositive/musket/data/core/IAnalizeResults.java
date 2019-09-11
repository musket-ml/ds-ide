package com.onpositive.musket.data.core;


public interface IAnalizeResults {
	
	IDataSet get(int num);
	
	int size();

	String[] names();

	default String visualizationSpec() {return "";}

	IDataSet getOriginal();
}
