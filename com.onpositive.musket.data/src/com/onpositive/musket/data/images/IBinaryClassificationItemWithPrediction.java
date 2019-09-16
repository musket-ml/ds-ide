package com.onpositive.musket.data.images;

import com.onpositive.musket.data.core.IItem;

public interface IBinaryClassificationItemWithPrediction extends IBinaryClasificationItem{


	public boolean isPredictionPositive();
	
	public IItem getPrediction();
}
