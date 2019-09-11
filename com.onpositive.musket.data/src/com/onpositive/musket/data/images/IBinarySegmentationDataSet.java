package com.onpositive.musket.data.images;

import java.util.Collection;

import com.onpositive.musket.data.core.IDataSet;

public interface IBinarySegmentationDataSet extends IDataSet,IBinaryClassificationDataSet{

	public Collection<? extends IBinarySegmentationItem> items();
	
	default double balance() {
		int positiveCount=0;
		for (IBinarySegmentationItem i:items()) {
			if (i.isPositive()) {
				positiveCount=positiveCount+1;
			}
		}
		return positiveCount/(double)this.items().size();
	}
}
