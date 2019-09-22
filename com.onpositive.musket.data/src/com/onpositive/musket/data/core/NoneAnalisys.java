package com.onpositive.musket.data.core;

public class NoneAnalisys implements IAnalizeResults{

	protected IDataSet dataset;
	
	public NoneAnalisys(IDataSet dataset) {
		super();
		this.dataset = dataset;
	}

	@Override
	public IDataSet get(int num) {
		return dataset;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public String[] names() {
		return new String[] {dataset.name()};
	}

	@Override
	public IDataSet getOriginal() {
		return dataset;
	}

	@Override
	public IDataSet getFiltered() {
		return dataset;
	}

}
