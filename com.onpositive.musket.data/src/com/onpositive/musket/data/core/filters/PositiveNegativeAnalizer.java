package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IBinaryClasificationItem;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Group by positive/negative split")
public class PositiveNegativeAnalizer extends AbstractAnalizer implements IAnalizer<IBinaryClassificationDataSet> {

	

	protected Object group(IItem v) {
		IBinaryClasificationItem bi=(IBinaryClasificationItem) v;
		return bi.isPositive();
	}

}
