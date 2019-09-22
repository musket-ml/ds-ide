package com.onpositive.musket.data.table;

import java.util.List;

import com.onpositive.musket.data.core.IItem;

public interface ICSVOVerlay {

	ITabularDataSet original();
	
	public List<ITabularItem>represents(IItem i);
}
