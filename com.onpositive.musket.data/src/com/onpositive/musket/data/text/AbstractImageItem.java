package com.onpositive.musket.data.text;

import com.onpositive.musket.data.core.AbstractItem;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.IImageItem;

public abstract class AbstractImageItem<T extends IDataSet> extends AbstractItem<T> implements IImageItem{

	public AbstractImageItem(T owner) {
		super(owner);
	}

}
