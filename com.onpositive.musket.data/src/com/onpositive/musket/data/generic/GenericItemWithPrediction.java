package com.onpositive.musket.data.generic;

import com.onpositive.musket.data.table.ITabularItem;

public class GenericItemWithPrediction extends GenericItem{

	protected GenericItem prediction;
	
	public GenericItemWithPrediction(GenericDataSet ds, ITabularItem base,GenericItem prediction) {
		super(ds, base);
		this.prediction=prediction;
	}

	public GenericItem getPrediction() {
		return prediction;
	}
	
}
