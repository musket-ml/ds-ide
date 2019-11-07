package com.onpositive.dside.dto;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import com.onpositive.dside.tasks.PrivateServerTask;

public class DataSetValidation extends PrivateServerTask<DataSetValidationResult>{

	boolean debug;
	
	protected IProject project;
	
	protected ArrayList<String>datasetNames;
	
	protected boolean validateOriginal;
	protected boolean validateAfterPreprocessing;
	protected int workers;
	
	public ArrayList<String> getDatasetNames() {
		return datasetNames;
	}

	public void setDatasetNames(ArrayList<String> datasetNames) {
		this.datasetNames = datasetNames;
	}

	@Override
	public Class<DataSetValidationResult> resultClass() {
		return DataSetValidationResult.class;
	}

	@Override
	public void afterCompletion(DataSetValidationResult taskResult) {
		
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	@Override
	public IProject[] getProjects() {
		return new IProject[] {project};
	}

	public boolean isValidateOriginal() {
		return validateOriginal;
	}

	public void setValidateOriginal(boolean validateOriginal) {
		this.validateOriginal = validateOriginal;
	}

	public boolean isValidateAfterPreprocessing() {
		return validateAfterPreprocessing;
	}

	public void setValidateAfterPreprocessing(boolean validateAfterPreprocessing) {
		this.validateAfterPreprocessing = validateAfterPreprocessing;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}