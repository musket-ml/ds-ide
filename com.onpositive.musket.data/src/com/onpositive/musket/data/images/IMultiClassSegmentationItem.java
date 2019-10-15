package com.onpositive.musket.data.images;

import java.util.List;

public interface IMultiClassSegmentationItem extends IImageItem,IMulticlassClassificationItem,IBinaryClasificationItem,IBinarySegmentationItem{

	BinarySegmentationItem getItem(String clazz);
	
	List<? extends ISegmentationItem> items(); 
	
}
