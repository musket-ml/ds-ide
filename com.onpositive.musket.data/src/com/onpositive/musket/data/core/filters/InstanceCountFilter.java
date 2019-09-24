package com.onpositive.musket.data.core.filters;

import java.util.function.Predicate;

import com.onpositive.musket.data.images.IInstanceSegmentationItem;



public class InstanceCountFilter implements Predicate<IInstanceSegmentationItem>{
	
	protected int minumum=0;
	protected int maximum=-1;

	@Override
	public boolean test(IInstanceSegmentationItem t) {
		return false;
	}

}
