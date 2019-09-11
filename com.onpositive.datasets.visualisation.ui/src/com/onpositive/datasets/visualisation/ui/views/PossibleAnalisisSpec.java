package com.onpositive.datasets.visualisation.ui.views;

import java.util.ArrayList;

public class PossibleAnalisisSpec {

	protected ArrayList<InstrospectedFeature>visualizers=new ArrayList<>();
	protected ArrayList<InstrospectedFeature>analizers=new ArrayList<>();
	protected ArrayList<InstrospectedFeature> data_analizers=new ArrayList<>();
	protected ArrayList<InstrospectedFeature>datasetFilters=new ArrayList<>();
	
	protected ArrayList<String>datasetStages=new ArrayList<String>();
	
	public ArrayList<InstrospectedFeature> getVisualizers() {
		return visualizers;
	}
	public void setVisualizers(ArrayList<InstrospectedFeature> visualizers) {
		this.visualizers = visualizers;
	}
	public ArrayList<InstrospectedFeature> getAnalizers() {
		return analizers;
	}
	public void setAnalizers(ArrayList<InstrospectedFeature> analizers) {
		this.analizers = analizers;
	}
	public ArrayList<InstrospectedFeature> getData_analizers() {
		return data_analizers;
	}
	public void setData_analizers(ArrayList<InstrospectedFeature> data_analizers) {
		this.data_analizers = data_analizers;
	}
	
	public InstrospectedFeature getVisualizer(String visualizer) {
		for (InstrospectedFeature f:visualizers) {
			if (f.getName().equals(visualizer)) {
				return f;
			}
		}
		return null;
	}
	
	public InstrospectedFeature getAnalizer(String visualizer) {
		for (InstrospectedFeature f:analizers) {
			if (f.getName().equals(visualizer)) {
				return f;
			}
		}
		for (InstrospectedFeature f:data_analizers) {
			if (f.getName().equals(visualizer)) {
				return f;
			}
		}
		return null;
	}
	public ArrayList<String> getDatasetStages() {
		return datasetStages;
	}
	
	public void setDatasetStages(ArrayList<String> datasetStages) {
		this.datasetStages = datasetStages;
	}
	
	public ArrayList<InstrospectedFeature> getDatasetFilters() {
		return datasetFilters;
	}
	
	public void setDatasetFilters(ArrayList<InstrospectedFeature> datasetFilters) {
		this.datasetFilters = datasetFilters;
	}
}