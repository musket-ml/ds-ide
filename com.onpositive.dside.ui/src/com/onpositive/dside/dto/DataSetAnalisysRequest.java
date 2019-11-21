package com.onpositive.dside.dto;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.dside.ui.ModelEvaluationSpec;

public class DataSetAnalisysRequest extends AbstractDataSetRequest {

	private String visualizer;
	private String analizer;
	private boolean isData;
	private String stage;
	
	protected HashMap<String, Object>visualizerArgs=new HashMap<>();
	protected HashMap<String, Object>analzierArgs=new HashMap<>();
	
	
	protected ArrayList<DataSetFilter>filters=new ArrayList<>();
	
	public DataSetAnalisysRequest(ModelEvaluationSpec model, String dataset,String experimentPath,String visualizer,String analizer,boolean isData,String stage) {
		super(model, dataset, experimentPath);
		this.visualizer=visualizer;
		this.analizer=analizer;
		this.isData=isData;
		this.stage=stage;
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
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	
	public ArrayList<DataSetFilter> getFilters() {
		return filters;
	}
	public void setFilters(ArrayList<DataSetFilter> filters) {
		this.filters = filters;
	}
}
