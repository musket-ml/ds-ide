package com.onpositive.musket.data.text;

import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.musket.data.table.ITabularItem;

public class TextItemWithPrediction extends TextItem implements IBinaryClassificationItemWithPrediction{
	

	private TextItem prediction;
	
	public TextItemWithPrediction(AbstractTextDataSet textDataSet, ITabularItem baseItem,TextItem prediction) {
		super(textDataSet, baseItem);
		this.prediction=prediction;		
	}

	@Override
	public boolean isPredictionPositive() {
		return prediction.isPositive();
	}

	@Override
	public IItem getPrediction() {
		return prediction;
	} 

}
