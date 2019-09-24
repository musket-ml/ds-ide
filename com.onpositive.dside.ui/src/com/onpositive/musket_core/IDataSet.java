package com.onpositive.musket_core;

public interface IDataSet {

	int len();

	String config();
	
	Object item(int num);
	
	Object id(int num);

	String get_name();
}
