package com.onpositive.musket.data.images;

import com.onpositive.musket.data.core.IItem;

public interface IBinaryClasificationItem extends IItem{

	public boolean isPositive();

	public default Object binaryLabel() {return isPositive();}
}
