package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.musket.data.images.MultiClassSegmentationItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Split items by class combination")
public class HasClassesAnalizer extends AbstractAnalizer implements IAnalizer<IMulticlassClassificationDataSet>{

	@SuppressWarnings({ "rawtypes" })
	@Override
	protected Object group(IItem v) {
		IMulticlassClassificationItem item=(IMulticlassClassificationItem) v;
		return (ArrayList)item.classes();
	}

}
