package com.onpositive.musket.data.images;

import java.util.ArrayList;

import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.ITabularItem;

public class MultiClassificationItemWithGroundTruth extends BinaryClassificationItemWithGroundTruth implements IMulticlassClassificationItem,IBinaryClasificationItem{

	public MultiClassificationItemWithGroundTruth(MultiClassificationDataSetWithGroundTruth binarySegmentationDataSet, ITabularItem v,
			ITabularItem prediction) {
		super(binarySegmentationDataSet, v, prediction);
	}

	@Override
	public ArrayList<String> classes() {
		String value = (String) this.base.clazzColumn.getValue(item);
		return MultiClassClassificationItem.splitByClass(value,this.base.labels);
	}
	
	public IItem getPrediction() {
		return new MultiClassClassificationItem(base, prediction);
	}
	
}
