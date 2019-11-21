package com.onpositive.dside.dto;

import com.onpositive.dside.ui.ModelEvaluationSpec;

public abstract class AbstractDataSetRequest {

	protected ModelEvaluationSpec modelSpec;
	protected String datasetName;
	protected String experimentPath;

	public AbstractDataSetRequest(ModelEvaluationSpec model, String dataset, String experimentPath) {
		this.modelSpec=model;
		this.datasetName=dataset;
		this.experimentPath=experimentPath;
	}

	public ModelEvaluationSpec getSpec() {
		return modelSpec;
	}

	public void setSpec(ModelEvaluationSpec spec) {
		this.modelSpec = spec;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getExperimentPath() {
		return experimentPath;
	}

	public void setExperimentPath(String experimentPath) {
		this.experimentPath = experimentPath;
	}

}