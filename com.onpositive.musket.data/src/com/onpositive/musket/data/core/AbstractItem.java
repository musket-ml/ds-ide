package com.onpositive.musket.data.core;

public abstract class AbstractItem<T extends IDataSet> implements Cloneable,IItem{

	protected T owner;

	public AbstractItem(T owner) {
		super();
		this.owner=owner;
	}

	public IItem clone() {
		try {
			return (IItem) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void setOwner(T ds) {
		this.owner=ds;
	}
	
	@Override
	public final IDataSet getDataSet() {
		return owner;
	}

}