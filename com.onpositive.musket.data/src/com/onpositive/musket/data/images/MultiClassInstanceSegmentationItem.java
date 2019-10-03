package com.onpositive.musket.data.images;

import java.util.ArrayList;

import com.onpositive.musket.data.table.ITabularItem;

public class MultiClassInstanceSegmentationItem extends MultiClassSegmentationItem{

	public MultiClassInstanceSegmentationItem(String id, MultiClassSegmentationDataSet binarySegmentationDataSet,
			ArrayList<ITabularItem> items) {
		super(id, binarySegmentationDataSet, items);
	}

}
