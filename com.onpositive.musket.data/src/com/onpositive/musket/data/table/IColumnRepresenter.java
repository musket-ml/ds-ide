package com.onpositive.musket.data.table;


public interface IColumnRepresenter {

	boolean like(IColumn c);
	
	public Object adaptValue(Object value);

}
