package com.onpositive.dside.dto;

import java.util.HashMap;

import com.onpositive.dside.ui.ModelEvaluationSpec;

public class DataSetAnalisysRequest {

	protected ModelEvaluationSpec modelSpec;
	protected String datasetName;
	protected String experimentPath;
	private String visualizer;
	private String analizer;
	private boolean isData;
	
	protected HashMap<String, Object>visualizerArgs=new HashMap<>();
	protected HashMap<String, Object>analzierArgs=new HashMap<>();
	
	public DataSetAnalisysRequest(ModelEvaluationSpec model, String dataset,String experimentPath,String visualizer,String analizer,boolean isData) {
		this.modelSpec=model;
		this.datasetName=dataset;
		this.experimentPath=experimentPath;
		this.visualizer=visualizer;
		this.analizer=analizer;
		this.isData=isData;
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
	public String getVisualizer() {
		return visualizer;
	}
	public void setVisualizer(String visualizer) {
		this.visualizer = visualizer;
	}
	public String getAnalizer() {
		return analizer;
	}
	public void setAnalizer(String analizer) {
		this.analizer = analizer;
	}
	public boolean isData() {
		return isData;
	}
	public void setData(boolean isData) {
		this.isData = isData;
	}
	public HashMap<String, Object> getVisualizerArgs() {
		return visualizerArgs;
	}
	public void setVisualizerArgs(HashMap<String, Object> visualizerArgs) {
		this.visualizerArgs = visualizerArgs;
	}
	public HashMap<String, Object> getAnalzierArgs() {
		return analzierArgs;
	}
	public void setAnalzierArgs(HashMap<String, Object> analzierArgs) {
		this.analzierArgs = analzierArgs;
	}
}
