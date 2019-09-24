package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IInstanceSegmentationDataSet;
import com.onpositive.musket.data.images.IInstanceSegmentationItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Group images by instance count")
public class InstanceCountAnalizer extends AbstractAnalizer implements IAnalizer<IInstanceSegmentationDataSet> {

	

	protected Object group(IItem v) {
		IInstanceSegmentationItem bi=(IInstanceSegmentationItem) v;
		return bi.items().size();		
	}

}
