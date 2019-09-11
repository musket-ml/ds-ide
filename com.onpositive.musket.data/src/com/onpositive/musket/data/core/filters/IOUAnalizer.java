package com.onpositive.musket.data.core.filters;

import java.text.NumberFormat;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.BinarySegmentationDataSetWithGroundTruth;
import com.onpositive.musket.data.images.BinarySegmentationItemWithGroundTruth;
import com.onpositive.musket.data.images.IBinaryClasificationItem;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Group by mask/prediction iou")
public class IOUAnalizer extends AbstractAnalizer implements IAnalizer<BinarySegmentationDataSetWithGroundTruth> {

	

	protected Object group(IItem v) {
		BinarySegmentationItemWithGroundTruth m1=(BinarySegmentationItemWithGroundTruth) v;
		if (m1.isPositive()&&m1.isPredictionPositive()) {
			double iou = m1.getMask().iou(m1.getPredictionMask());
			int rr=(int) Math.ceil(iou*10);
			if (iou==0) {
				return 0;
			}
			return NumberFormat.getInstance().format(((rr*0.1)-0.1))+"-"+NumberFormat.getInstance().format((rr*0.1));
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
