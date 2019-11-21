package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Split items by classes")
public class HasClassAnalizer extends AbstractMultiSplitAnalizer implements IAnalizer<IMulticlassClassificationDataSet>{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ArrayList<Object> group(IItem v) {
		IMulticlassClassificationItem item= (IMulticlassClassificationItem) v;
		return new ArrayList<>(new LinkedHashSet<>(item.classes()));
	}

}
