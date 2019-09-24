package com.onpositive.musket.data.images;

import java.util.Collection;


public interface IObjectDetectionItem extends IImageItem{

	public Collection<? extends IObjectDetectionObject>objects();
}
