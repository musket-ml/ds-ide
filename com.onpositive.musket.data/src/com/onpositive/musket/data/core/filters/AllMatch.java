package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.generic.GenericItemWithPrediction;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Prediction matches with Ground Truth")
public class AllMatch extends AbstractAnalizer implements IAnalizer<IDataSetWithGroundTruth>, ICustomApply {

	@Override
	protected Object group(IItem v) {
		GenericItemWithPrediction p1 = (GenericItemWithPrediction) v;
		if (p1.getPrediction()==null) {
			return "Prediction not found";
		}
		if (p1.allMatch()) {
			return "All Match";
		} else {
			return "Has differences";
		}
	}

	@Override
	public boolean canApply(IDataSet d) {
		if (!(d instanceof GenericDataSet)) {
			return false;
		}
		return true;
	}

}
