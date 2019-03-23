package com.onpositive.musket_core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class ExperimentResults extends Result {

	private StageResult allStages = null;
	private ArrayList<StageResult> results = new ArrayList<>();
	private String name;
	private boolean all;

	public ExperimentResults(String name, String path) {
		this.name = name;
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream;
			try {
				inputStream = new FileInputStream(path);
				Object load = yaml.load(inputStream);
				if (load instanceof Map) {
					Map<String, Object> obj = (Map<String, Object>) load;
					if (obj != null && obj.containsKey("allStages")) {
						Object object = obj.get("allStages");
						StageResult stageResult = new StageResult("All Stages", object);
						allStages = stageResult;
						stageResult.setAllStages(true);
						results.add(stageResult);
						Object object2 = obj.get("stages");
						if (object2 instanceof List) {
							List l = (List) object2;
							for (int i = 0; i < l.size(); i++) {
								results.add(new StageResult("Stage " + i, l.get(i)));
							}
						}
					} else {
						StageResult stageResult = new StageResult("All Attempts", obj);
						stageResult.setAllAttempts(true);
						this.allStages = stageResult;
						this.results.add(stageResult);
						this.all = true;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	@Override
	public Object getMetric(String name) {
		if (name.equals("Attempt")) {
			return this.name;
		}
		if (this.allStages != null) {
			return this.allStages.getMetric(name);
		}
		return null;
	}

	public ArrayList<String> getMetrics() {
		HashSet<String> string = new HashSet<>();
		for (StageResult r : results) {
			string.addAll(r.metrics());
		}
		return new ArrayList<>(string);
	}
}
