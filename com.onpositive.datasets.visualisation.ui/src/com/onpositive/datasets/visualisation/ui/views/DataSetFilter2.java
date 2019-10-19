package com.onpositive.datasets.visualisation.ui.views;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/filter2.dlf")
public class DataSetFilter2 extends DataSetFilter{

	public DataSetFilter2(ArrayList<InstrospectedFeature> features) {
		super(features);
		features.forEach(v->{
			kinds.add(v.getName());
		});
	}

	
}
