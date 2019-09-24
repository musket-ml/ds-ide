package com.onpositive.musket.data.core;


public interface IStatisticProperty<T extends IDataSet> {

	public void append(IItem o);
	
	public String caption();
	
	public Object value();
}
