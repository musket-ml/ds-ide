package com.onpositive.musket.data.core;

public interface IItem extends Cloneable{

	String id();

	IDataSet getDataSet();
	
	IItem clone();
	
	
}
