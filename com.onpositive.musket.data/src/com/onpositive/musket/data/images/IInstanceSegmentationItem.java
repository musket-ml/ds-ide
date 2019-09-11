package com.onpositive.musket.data.images;

import java.util.List;

public interface IInstanceSegmentationItem extends IImageItem{

	List<? extends ISegmentationItem> items();

}
