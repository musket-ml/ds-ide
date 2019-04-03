package com.onpositive.dside.tasks.analize;

import com.onpositive.musket_core.IDataSet;

public interface IAnalizeResults {
	
	IDataSet get(int num);
	
	int size();

	String[] names();
}
