package com.onpositive.datasets.visualisation.ui.views;

import org.eclipse.core.resources.IFolder;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;

public abstract class GenericExperimentTemplate {

	@Caption("Name")
	protected String name = "newExperiment";
	@Caption("Classes")
	protected int numClasses = 1;
	protected String loss = "binary_crossentropy";

	@Caption("Activation")
	@RealmProvider(ActivationRealmProvider.class)
	protected String activation="sigmoid";
	
	public GenericExperimentTemplate() {
		super();
	}
	public String projectPath;
	public abstract String finish() ;
	
	public void finishExperimentFolder(IFolder folder) {}

}