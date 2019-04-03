package com.onpositive.dside.dto.introspection;

import java.util.ArrayList;

public class InstrospectionResult {

	ArrayList<InstrospectedFeature> features=new ArrayList<>();

	public ArrayList<InstrospectedFeature> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<InstrospectedFeature> features) {
		this.features = features;
	}
}
