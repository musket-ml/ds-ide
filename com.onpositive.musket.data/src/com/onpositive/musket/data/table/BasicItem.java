package com.onpositive.musket.data.table;

import java.util.Arrays;

import com.onpositive.musket.data.core.IDataSet;

public class BasicItem implements ITabularItem{

	BasicDataSetImpl source;
	
	protected BasicItemExtra extra;
	
	public BasicItem(int id, Object[] values) {
		super();
		this.id = id;
		this.values = values;
	}

	protected int id;
	
	protected Object[] values;
	

	@Override
	public String id() {
		if (source.idColumn!=-1) {
			return this.values[source.idColumn].toString();
		}
		return ""+id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicItem other = (BasicItem) obj;
		if (!Arrays.deepEquals(values, other.values))
			return false;
		return true;
	}

	@Override
	public IDataSet getDataSet() {
		return source;
	}

	@Override
	public Object value(String string) {
		return source.getColumn(string).getValue(this);
	}
	
	@Override
	public String toString() {
		return this.id()+":"+Arrays.deepToString(this.values);
	}

	@Override
	public int num() {
		return id;
	}

}
