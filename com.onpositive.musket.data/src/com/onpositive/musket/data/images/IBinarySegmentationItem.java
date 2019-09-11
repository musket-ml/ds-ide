package com.onpositive.musket.data.images;

public interface IBinarySegmentationItem extends IImageItem,IBinaryClasificationItem{

	IMask getMask();
}
