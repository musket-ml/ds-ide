package com.onpositive.dside.ui.editors.yaml.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.onpositive.semantic.model.api.meta.BaseMeta;

public class NodeType {

	protected Map<String,PropertyDescription>properties=new LinkedHashMap<>();

	public Map<String, PropertyDescription> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PropertyDescription> properties) {
		this.properties = properties;
	}

	
	
	

}
