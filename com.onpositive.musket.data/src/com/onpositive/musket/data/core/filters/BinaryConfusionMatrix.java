package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.generic.GenericItemWithPrediction;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Binary confusion matrics")
public class BinaryConfusionMatrix extends AbstractAnalizer implements IAnalizer<IDataSetWithGroundTruth> ,ICustomApply{

	@Override
	protected Object group(IItem v) {
		if (v instanceof IBinaryClassificationItemWithPrediction) {

			IBinaryClassificationItemWithPrediction m1 = (IBinaryClassificationItemWithPrediction) v;
			if (m1 instanceof IMulticlassClassificationItem) {
				IMulticlassClassificationItem item = (IMulticlassClassificationItem) m1;
				ArrayList<String> classes = item.classes();
				IMulticlassClassificationDataSet iMulticlassClassificationDataSet = (IMulticlassClassificationDataSet) item
						.getDataSet();
				if (iMulticlassClassificationDataSet.classNames().size() == 2
						&& iMulticlassClassificationDataSet.isExclusive()) {
					return new ClassConfusionMatrix().group(v);
				}
				IMulticlassClassificationItem prediction = (IMulticlassClassificationItem) m1.getPrediction();
				if (prediction==null) {
					return "Unmatched";
				}
				ArrayList<String> classes2 = prediction.classes();
				HashSet<String> gt = new HashSet<>(classes);
				HashSet<String> pr = new HashSet<>(classes2);

				if (pr.equals(gt)) {
					if (pr.size() == 1 && pr.contains("Empty")) {
						return "True negative";
					}
					return "All matched";
				}
				Collections.sort(classes);
				Collections.sort(classes2);
				return "Has classification errors";
			}
			if (m1.isPositive() && m1.isPredictionPositive()) {
				return "True positive";
			}
			if (!m1.isPositive() && !m1.isPredictionPositive()) {
				return "True negative";
			}
			if (m1.isPositive() && !m1.isPredictionPositive()) {
				return "False negative";
			}
			if (!m1.isPositive() && m1.isPredictionPositive()) {
				return "False positive";
			}
			throw new IllegalStateException();
		}
		else {
			GenericItemWithPrediction p1=(GenericItemWithPrediction) v;
			if (p1.allMatch()) {
				return "All Match";
			}
			else {
				return "Has differences";
			}
		}
	}

	@Override
	public boolean canApply(IDataSet d) {
		if (d instanceof GenericDataSet) {
			return false;
		}
		return true;
	}

}
