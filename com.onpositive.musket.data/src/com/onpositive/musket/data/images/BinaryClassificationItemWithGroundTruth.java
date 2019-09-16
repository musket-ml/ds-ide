package com.onpositive.musket.data.images;

import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.ITabularItem;

public class BinaryClassificationItemWithGroundTruth extends BinaryClassificationItem  implements IBinaryClassificationItemWithPrediction{

	protected ITabularItem prediction;

	public BinaryClassificationItemWithGroundTruth(BinaryClassificationDataSet binarySegmentationDataSet,
			ITabularItem v,ITabularItem prediction) {
		super(binarySegmentationDataSet, v);
		this.prediction=prediction;
	}

	@Override
	public boolean isPredictionPositive() {
		return super.isPositiveValue(base.clazzColumn.getValue(prediction));
	}

	public IItem getPrediction() {
		return new BinaryClassificationItem(base, prediction);
	}



}
