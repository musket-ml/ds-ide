package com.onpositive.dside.dto;

import com.onpositive.dside.ui.ModelEvaluationSpec;

public class ExportDataSet {

	protected ModelEvaluationSpec modelSpec;
	protected String datasetName;
	protected String experimentPath;
	
	protected boolean exportGroundTruth;
	
	public boolean isExportGroundTruth() {
		return exportGroundTruth;
	}
	public void setExportGroundTruth(boolean exportGroundTruth) {
		this.exportGroundTruth = exportGroundTruth;
	}
	public ExportDataSet(ModelEvaluationSpec model, String dataset,String experimentPath,boolean exportGroundTruth) {
		this.modelSpec=model;
		this.datasetName=dataset;
		this.experimentPath=experimentPath;
		this.exportGroundTruth=exportGroundTruth;
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
