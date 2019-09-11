package com.onpositive.musket.data.table;

import java.util.Collection;

public interface IColumn {

	public String id();

	public String caption();
	
	public Class<?>clazz();

	public Object getValue(ITabularItem item);
	public void setValue(ITabularItem item,Object value);
	public String getValueAsString(ITabularItem i);
	
	public ITabularDataSet owner();

	public Collection<Object> values();

	public IColumn clone();
}
