package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Collection;

public class Column implements IColumn,Cloneable{

	protected String id;
	protected String caption;
	protected int num;
	protected Class<?>clazz;
	protected ITabularDataSet owner;
	
	@Override
	public String id() {
		return id;
	}

	public IColumn clone() {
		try {
			return (IColumn) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}		
	}
	
	@Override
	public String caption() {
		if (this.caption==null) {
			return this.id;
		}
		return caption;
	}

	@Override
	public Class<?> clazz() {
		return clazz;
	}

	@Override
	public Object getValue(ITabularItem item) {
		BasicItem bi=(BasicItem) item;
		return bi.values[num];
	}
	@Override
	public void setValue(ITabularItem item, Object value) {
		BasicItem bi=(BasicItem) item;
		bi.values[num]=clazz.cast(value);
	}
	public ITabularDataSet owner() {
		return owner;
	}

	

	public Column(String id, String caption, int num, Class<?> clazz) {
		super();
		this.id = id;
		this.caption = caption;
		this.num = num;
		this.clazz = clazz;
	}

	public Object parse(String string) {
		return string;
	}

	@Override
	public String getValueAsString(ITabularItem i) {
		Object vl=getValue((ITabularItem) i);
		if (vl==null) {
			return "";
		}
		return vl.toString();
	}

	@Override
	public Collection<Object> values() {
		ArrayList<Object>result=new ArrayList<Object>();
		this.owner.items().forEach(v->{
			result.add(getValue(v));
		});
		return result;
	}
}