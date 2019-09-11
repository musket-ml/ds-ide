package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IStatisticProperty;

public class BinaryStatisticProperties implements IStatisticProperty<IDataSetWithGroundTruth>{

	int positiveCount;
	int allCount;
	
	@Override
	public void append(IItem o) {
		IDataSetWithGroundTruth gd=(IDataSetWithGroundTruth) o;
		
	}

	@Override
	public String caption() {
		return "Binary Accuracy";
	}

	@Override
	public Object value() {
		return positiveCount/allCount;
	}

	
}
