package com.onpositive.dside.wizards;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/kaggleRun.dlf")
public class KaggleRunConfig {
	@Caption("Project")
	@Required
	String project;
	
	@Caption("Kaggle Username")
	@Required
	String username = "username";
	
	@Caption("Kagle kernels name prefix")
	@Required
	String project_id = "project-id";
	
	@Caption("Experiment Name")
	@Required
	String experiment = "experiments/some_experiment";
	
	@Caption("Num of Kaggle kernels to run")
	@Required
	int kernels = 0;
	
	String datasource = "";
	
	String datasourceType = "";
	
	@Caption("One kernel per train fold")
	@Required
	Boolean split_by_folds = true;
	
	@Caption("HTTP Server for messages exchange")
	@Required
	String server = "192.0.0.1";
	
	@Caption("HTTP Server local port")
	@Required
	int port = 9393;
	
	@Caption("Delay between HTTP requests")
	@Required
	int requests_delay = 10;
	
	@Caption("Kaggle kernel working time")
	@Required
	int time = 600;
	
	public String serializeToJsonString() {
		JsonParser jsonParser = new JsonParser();
		
		JsonElement root = jsonParser.parse("{}");
		
		root.getAsJsonObject().addProperty("username", this.username);
		root.getAsJsonObject().addProperty("project_id", this.project_id);
		root.getAsJsonObject().addProperty("experiment", this.experiment);
		root.getAsJsonObject().addProperty("kernels", this.kernels);
		root.getAsJsonObject().addProperty("split_by_folds", this.split_by_folds);
		root.getAsJsonObject().addProperty("server", this.server);
		root.getAsJsonObject().addProperty("port", this.port);
		root.getAsJsonObject().addProperty("requests_delay", this.requests_delay);
		root.getAsJsonObject().addProperty("time", this.time);
		
		if(datasource.length() > 0) {
			if(datasourceType == "dataset") {
				root.getAsJsonObject().add("dataset_sources", jsonParser.parse("[\"" + datasource + "\"]"));
				root.getAsJsonObject().add("competition_sources", jsonParser.parse("[]"));
			} else {
				root.getAsJsonObject().add("dataset_sources", jsonParser.parse("[]"));
				root.getAsJsonObject().add("competition_sources", jsonParser.parse("[\"" + datasource + "\"]"));
			}
		} else {
			root.getAsJsonObject().add("dataset_sources", jsonParser.parse("[]"));
			root.getAsJsonObject().add("competition_sources", jsonParser.parse("[]"));
		}
		
		root.getAsJsonObject().add("kernel_sources", jsonParser.parse("[]"));
		
		return root.toString();
	}
	
	public void deserializeFromJsonString(String jsonString) {
		JsonParser jsonParser = new JsonParser();
		
		JsonElement root = jsonParser.parse(jsonString);
				
		this.username = root.getAsJsonObject().get("username").getAsString();
		this.project_id = root.getAsJsonObject().get("project_id").getAsString();
		this.experiment = root.getAsJsonObject().get("experiment").getAsString();
		this.kernels = root.getAsJsonObject().get("kernels").getAsInt();
		this.split_by_folds = root.getAsJsonObject().get("split_by_folds").getAsBoolean();
		this.server = root.getAsJsonObject().get("server").getAsString();
		this.port = root.getAsJsonObject().get("port").getAsInt();
		this.requests_delay = root.getAsJsonObject().get("requests_delay").getAsInt();
		this.time = root.getAsJsonObject().get("time").getAsInt();
		
		JsonArray competitionsSources = root.getAsJsonObject().get("competition_sources").getAsJsonArray();
		JsonArray datasetsSources = root.getAsJsonObject().get("dataset_sources").getAsJsonArray();
		
		if(datasetsSources.size() > 0) {
			this.datasourceType = "dataset";
			
			this.datasource = datasetsSources.get(0).getAsString();
		} else if(competitionsSources.size() > 0) {
			this.datasourceType = "competition";
			
			this.datasource = competitionsSources.get(0).getAsString();
		}
	}
}