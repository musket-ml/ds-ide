package com.onpositive.musket.data.project;

import java.util.ArrayList;

import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Required;


@Display("dlf/factories.dlf")
public class FactoryModel {
	
	public FactoryModel(ArrayList<IDataSetFactory> matching) {
		this.factories.addAll(matching);
	}

	protected ArrayList<IDataSetFactory>factories=new ArrayList<>();
	
	@Required("Please select dataset factory to use")
	protected IDataSetFactory selected;
	
}
