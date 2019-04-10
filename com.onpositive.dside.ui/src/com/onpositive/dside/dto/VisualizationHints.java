package com.onpositive.dside.dto;

import java.util.ArrayList;
import java.util.Map;

public class VisualizationHints {

	protected String type;
	protected double total;
	
	protected ArrayList<String>series=new ArrayList<>();
	
	protected ArrayList<Map<Integer,Number>>values=new ArrayList<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public ArrayList<String> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<String> series) {
		this.series = series;
	}

	public ArrayList<Map<Integer, Number>> getValues() {
		return values;
	}

	public void setValues(ArrayList<Map<Integer, Number>> values) {
		this.values = values;
	}
	
	protected String x_axis;
	protected String y_axis;

	public String getX_axis() {
		return x_axis;
	}

	public void setX_axis(String x_axis) {
		this.x_axis = x_axis;
	}

	public String getY_axis() {
		return y_axis;
	}

	public void setY_axis(String y_axis) {
		this.y_axis = y_axis;
	}
}
