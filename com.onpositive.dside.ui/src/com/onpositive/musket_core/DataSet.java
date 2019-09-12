package com.onpositive.musket_core;

import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/dataset.dlf")
public class DataSet {

	private IDataSet dataset;

	public DataSet(IDataSet dataset) {
		super();
		this.dataset = dataset;
	}
	
	public int getLen() {
		return dataset.len();
	}
	
	public String getConfig() {
		return dataset.config();
	}
	
	public String getName() {
		return dataset.get_name();
	}

	public Object item(int i) {
		return dataset.item(i);
	}
	public Object id(int i) {
		return dataset.id(i);
	}
	
	@Override
	public String toString() {
		return dataset.get_name();
	}
}
