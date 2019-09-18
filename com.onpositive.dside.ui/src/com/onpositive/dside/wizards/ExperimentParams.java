package com.onpositive.dside.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.onpositive.musket_core.ProjectWrapper.BasicDataSetDesc;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/experimentInit.dlf")
public class ExperimentParams{

	@Caption("Project")
	@Required
	String project;
	
	@Caption("Experiment group")
	String group;
	
	@Required
	@Caption("Experiment name")
	String name;
	
	String description;
	
	@Required
	@Caption("Start from")
	String template;
	
	@Caption("DataSet")
	String dataset;
	
	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
		if (!dataset.isEmpty()) {
			
			System.out.println("A");
		}
	}

	public Collection<String>getTemplates(){
		return TemplatesList.getTemplatesList().getTemplates().stream().map(x->x.name).collect(Collectors.toList());		
	}
	
	
	ArrayList<String>possibleDataSets=new ArrayList<>();

	protected ArrayList<BasicDataSetDesc> datasets;
	
	public Collection<String>getDatasets(){
		return possibleDataSets;		
	}
	
}