package com.onpositive.musket.data.images;

import java.util.Collection;


public interface ISegmentationItem extends IImageItem{

	Collection<IMask>getMasks(); 

}