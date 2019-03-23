package com.onpositive.musket_core;

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
		return dataset.name();
	}

	public Object item(int i) {
		return dataset.item(i);
	}
}
