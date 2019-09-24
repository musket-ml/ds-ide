package com.onpositive.musket.data.images;

public interface IMultiClassSegmentationItem extends IImageItem,IMulticlassClassificationItem,IBinaryClasificationItem,IBinarySegmentationItem{

	BinarySegmentationItem getItem(String clazz);
	
}
