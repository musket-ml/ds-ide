package com.onpositive.musket.data.core;



public class VisualizationSpec {

	public static enum ChartType{
		BAR,PIE,TABLE
	}
	
	public final String yName;
	public final String xName;
	
	public ChartType type;
	public ChartData chart;
	public ChartData full;
	
	public VisualizationSpec(String yName, String xName, ChartType type) {
		super();
		this.yName = yName;
		this.xName = xName;
		this.type = type;
	}
	
}
