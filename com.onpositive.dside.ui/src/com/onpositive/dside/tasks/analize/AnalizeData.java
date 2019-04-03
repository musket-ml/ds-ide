package com.onpositive.dside.tasks.analize;

import com.onpositive.musket_core.Experiment;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/analizeData.dlf")
public class AnalizeData extends AnalizeDataSet{

	public AnalizeData(Experiment experiment) {
		super(experiment);
		this.data=true;
	}

}
