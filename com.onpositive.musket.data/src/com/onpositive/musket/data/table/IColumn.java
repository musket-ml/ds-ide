package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

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
	
	@SuppressWarnings("unchecked")
	public default ArrayList<Object>uniqueValues(){
		LinkedHashSet<Object>vl=new LinkedHashSet<>(this.values());
		ArrayList<Object>result=new ArrayList<>(vl);
		Collections.sort((List)result);
		return result;
	}
}
