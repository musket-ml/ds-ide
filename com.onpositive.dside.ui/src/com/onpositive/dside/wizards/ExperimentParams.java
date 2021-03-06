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
	
	@Caption("Group")
	String group;
	
	@Required
	@Caption("Name")
	String name;
	
	String description;
	
	@Required
	@Caption("Template")
	String template;
	
	@Caption("DataSet")
	String dataset;
	
	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
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