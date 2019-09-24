package com.onpositive.musket.data.images;

import java.util.Collection;

public interface IInstanceSegmentationDataSet extends IImageDataSet{

	public Collection<? extends IInstanceSegmentationItem> items(); 
}
