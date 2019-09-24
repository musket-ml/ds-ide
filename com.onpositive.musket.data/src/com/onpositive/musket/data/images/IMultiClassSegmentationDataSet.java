package com.onpositive.musket.data.images;

import java.util.Collection;

public interface IMultiClassSegmentationDataSet extends IImageDataSet,IMulticlassClassificationDataSet,IBinaryClassificationDataSet{

	Collection<IMultiClassSegmentationItem> items();
}
