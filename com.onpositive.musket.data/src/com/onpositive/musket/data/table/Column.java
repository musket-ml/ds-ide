package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Column implements IColumn,Cloneable{

	protected String id;
	protected String caption;
	private int num;
	private Class<?>clazz;
	protected ITabularDataSet owner;
	private ArrayList<Object> result;
	
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
		return getClazz();
	}

	@Override
	public Object getValue(ITabularItem item) {
		BasicItem bi=(BasicItem) item;
		return bi.values[getNum()];
	}
	@Override
	public void setValue(ITabularItem item, Object value) {
		BasicItem bi=(BasicItem) item;
		bi.values[getNum()]=getClazz().cast(value);
	}
	public ITabularDataSet owner() {
		return owner;
	}

	

	public Column(String id, String caption, int num, Class<?> clazz) {
		super();
		this.id = id;
		this.caption = caption;
		this.setNum(num);
		this.setClazz(clazz);
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
		if (result!=null) {
			return result;
		}
		result =   (ArrayList<Object>) this.owner.items().parallelStream().map(v->{
			Object value = getValue(v);
			if (value==null) {
				return "";
			}
			return value;
		}).collect(Collectors.toList());
		return result;
	}
	ArrayList<Object>uniqueValues;
	
	@Override
	public ArrayList<Object> uniqueValues() {
		if (uniqueValues==null) {
			uniqueValues=IColumn.super.uniqueValues();
		}
		return uniqueValues;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	@Override
	public String toString() {
		if (this.caption==null) {
			return this.id;
		}
		return this.caption;
	}
}