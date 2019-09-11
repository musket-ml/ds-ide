package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IDataSetFilter;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Correctly classified items")
public class CorrectlyClassifierFilter implements IDataSetFilter<IDataSetWithGroundTruth>{

	@Override
	public boolean test(IItem t) {
		IBinaryClassificationItemWithPrediction bi=(IBinaryClassificationItemWithPrediction) t;
		return bi.isPositive()==bi.isPredictionPositive();
	}

}
