package com.onpositive.musket.data.text;

import com.onpositive.musket.data.core.AbstractItem;
import com.onpositive.musket.data.core.IDataSet;

public abstract class AbstractTextItem<T extends IDataSet> extends AbstractItem<T> implements ITextItem{

	public AbstractTextItem(T owner) {
		super(owner);
	}

}
