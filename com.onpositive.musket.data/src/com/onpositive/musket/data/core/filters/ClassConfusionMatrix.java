package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.musket.data.images.MultiClassificationDataSetWithGroundTruth;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Multiclass confusion matrics")
public class ClassConfusionMatrix extends AbstractAnalizer implements IAnalizer<MultiClassificationDataSetWithGroundTruth>{

	@Override
	protected Object group(IItem v) {
		IBinaryClassificationItemWithPrediction m1=(IBinaryClassificationItemWithPrediction) v;
		if (m1 instanceof IMulticlassClassificationItem) {
			IMulticlassClassificationItem item=(IMulticlassClassificationItem) m1;
			ArrayList<String> classes = item.classes();
			
			IMulticlassClassificationItem prediction=(IMulticlassClassificationItem) m1.getPrediction();
			ArrayList<String> classes2 = prediction.classes();			
			HashSet<String> gt = new HashSet<>(classes);
			HashSet<String> pr = new HashSet<>(classes2);
			
			if (pr.equals(gt)) {
				if (pr.size()==1&&pr.contains("Empty")) {
					return "True negative";
				}
				return "All matched";
			}
			Collections.sort(classes);
			Collections.sort(classes2);
			return classes.stream().map(x->x.toString()).collect(Collectors.joining(","))+"-"+classes2.stream().map(x->x.toString()).collect(Collectors.joining(","))+";";
		}
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
