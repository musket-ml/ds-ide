package com.onpositive.mmdetection.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MMDetCfgData {
	
	public MMDetCfgData(String architecture, String path) {
		super();
		this.architecture = architecture;
		this.path = path;
	}

	private String architecture;
	
	private String path;
	
	public String getArchitecture() {
		return architecture;
	}

	public String getPath() {
		return path;
	}

	private HashMap<String,String> parameters = new HashMap<String, String>();
	
	public void addParameter(String paramName, String paramValue) {
		this.parameters.put(paramName, paramValue);
	}
	
	public String getParameterValue(String paramName) {
		return this.parameters.get(paramName);
	}
	
	public List<String> paramNames(){
		return new ArrayList<String>(parameters.keySet());
	}
}
