package com.onpositive.musket.data.table;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;

public class BasicDelta implements IDataSetDelta{

	protected IDataSet additions;
	protected IDataSet removals;

	public BasicDelta(IDataSet additions, IDataSet removals, IDataSet changes) {
		super();
		this.additions = additions;
		this.removals = removals;
		this.changes = changes;
	}

	protected IDataSet changes;
	
	@Override
	public IDataSet additions() {
		return additions;
	}

	@Override
	public IDataSet removals() {
		return removals;
	}

	@Override
	public IDataSet changes() {
		return changes;
	}

}
