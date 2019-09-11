package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Binary confusion matrics")
public class BinaryConfusionMatrix extends AbstractAnalizer implements IAnalizer<IDataSetWithGroundTruth>{

	@Override
	protected Object group(IItem v) {
		IBinaryClassificationItemWithPrediction m1=(IBinaryClassificationItemWithPrediction) v;
		if (m1.isPositive()&&m1.isPredictionPositive()) {
			return "True positive";
		}
		if (!m1.isPositive()&&!m1.isPredictionPositive()) {
			return "True negative";
		}
		if (m1.isPositive()&&!m1.isPredictionPositive()) {
			return "False negative";
		}
		if (!m1.isPositive()&&m1.isPredictionPositive()) {
			return "False positive";
		}
		throw new IllegalStateException();
	}

}
