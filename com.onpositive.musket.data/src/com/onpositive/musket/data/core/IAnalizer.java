package com.onpositive.musket.data.core;

public interface IAnalizer<T extends IDataSet> {

	IAnalizeResults analize(IDataSet ds);
}
