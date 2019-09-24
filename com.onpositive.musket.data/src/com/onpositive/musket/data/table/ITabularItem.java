package com.onpositive.musket.data.table;

import com.onpositive.musket.data.core.IItem;

public interface ITabularItem extends IItem{

	
	public Object value(String string);
	
	public default double doubleValue(String string) {
		return Double.parseDouble(value(string).toString());
	}

	public int num();
}