package com.onpositive.dside.dto;

import java.util.ArrayList;

import com.onpositive.dside.dto.introspection.InstrospectedFeature;

public class GetPossibleAnalisisResult {

	protected ArrayList<InstrospectedFeature>visualizers=new ArrayList<>();
	protected ArrayList<InstrospectedFeature>analizers=new ArrayList<>();
	protected ArrayList<InstrospectedFeature> data_analizers=new ArrayList<>();
	
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
}
