package com.onpositive.dside.ui.datasets;

import java.util.ArrayList;
import java.util.LinkedHashMap;



public class ClassificationInfo {

	protected ArrayList<String>labels=new ArrayList<>();
	
	protected ArrayList<Object>values=new ArrayList<>();
	
	protected LinkedHashMap<Object, ArrayList<Object>>itemIds=new LinkedHashMap<>();
	
	protected ArrayList<Object>negatives=new ArrayList<>();
	
	protected boolean multiclass;
	
	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}

	public ArrayList<Object> getValues() {
		return values;
	}

	public void setValues(ArrayList<Object> values) {
		this.values = values;
	}

	public LinkedHashMap<Object, ArrayList<Object>> getItemIds() {
		return itemIds;
	}

	public void setItemIds(LinkedHashMap<Object, ArrayList<Object>> itemIds) {
		this.itemIds = itemIds;
	}

	public ArrayList<Object> getNegatives() {
		return negatives;
	}

	public void setNegatives(ArrayList<Object> negatives) {
		this.negatives = negatives;
	}

	public boolean isMulticlass() {
		return multiclass;
	}

	public void setMulticlass(boolean multiclass) {
		this.multiclass = multiclass;
	}
	
}
