package com.onpositive.musket_core;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.IMusketConstants;
import com.onpositive.dside.ui.ModelEvaluationSpec;
import com.onpositive.musket_core.ProjectWrapper.BasicDataSetDesc;
import com.onpositive.semantic.model.api.property.java.annotations.Image;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.yamledit.io.YamlIO;

@Image("icons/experiment.png")
@TextLabel(provider = ExperimentLabelProvider.class)
public class Experiment {

	private String path;
	private Map<String, Object> config;
	private Score _score;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	public String getProjectPath() {
		File file = new File(path);
		if (file.getName().equals("assets")) {
			return file.getParentFile().getAbsolutePath();
		}
		
		while (true) {
			if (file.getParentFile() == null) {
				return null;
			}
			File experimentsFolder = new File(file, IMusketConstants.MUSKET_EXPERIMENTS_FOLDER);
			if (experimentsFolder.exists() && experimentsFolder.isDirectory()) {
				return file.getAbsolutePath();
			}
			file = file.getParentFile();
			if (file.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
				return file.getParentFile().getAbsolutePath();
			}			
		}
	}

	public static class PredictionPair {
		public File groundTruth;
		public File prediction;
		public String name;

		@Override
		public String toString() {
			return groundTruth.getName() + "-" + prediction.getName();
		}
	}

	public List<PredictionPair> getPredictions() {
		File file = new File(path, "predictions");
		ArrayList<PredictionPair> ps = new ArrayList<>();
		HashMap<String, PredictionPair> psm = new HashMap<>();
		if (file.exists() && file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (f.getName().endsWith("-gt.csv")) {

					PredictionPair predictionPair = getName(psm, f);
					predictionPair.groundTruth = f;

				}
				if (f.getName().endsWith("-pt.csv") || f.getName().endsWith("-pr.csv")) {

					PredictionPair predictionPair = getName(psm, f);
					predictionPair.prediction = f;
				}
			}
			ps.addAll(psm.values());
			Collections.sort(ps, (x, y) -> x.groundTruth.getName().compareTo(y.groundTruth.getName()));
		}
		return ps;
	}

	protected PredictionPair getName(HashMap<String, PredictionPair> psm, File f) {
		String name = f.getName();
		// name=name.replace(" ", "");
		name = name.substring(0, name.length() - 7);
		PredictionPair predictionPair = psm.get(name);
		if (predictionPair == null) {
			predictionPair = new PredictionPair();
			predictionPair.name = name;
			psm.put(name, predictionPair);
		}
		return predictionPair;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Experiment other = (Experiment) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public Experiment(String path) {
		this.path = path;
	}

	public Score getScore() {
		if (this._score == null) {
			this._score = this.readScore();
		}
		return this._score;
	}

	private Score readScore() {

		File file = new File(path, "summary.yaml");
		if (!file.exists()) {
			return new Score("", this);
		}
		try {
			FileReader fileReader = new FileReader(file);
			try {
				Object load = YamlIO.load(fileReader);
				return new Score(load, this);
			} finally {
				fileReader.close();
			}

		} catch (Exception e) {
			return new Score("Error", this);
		}
	}

	public boolean isCompleted() {
		Score score = this.getScore();
		if (score.isRealScore()) {
			return true;
		}
		return false;
	}

	public Map<String, Object> getConfig() {
		if (this.config != null) {
			return config;
		}
		this.config = ExperimentIO.readConfig(path);
		return config;
	}

	public Errors getErrors() {
		File file = new File(path, "error.yaml");
		if (file.exists()) {
			try {
				FileReader fileReader = new FileReader(file);
				try {
					return YamlIO.loadAs(fileReader, Errors.class);
				} finally {
					fileReader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new Errors();
			}
		}
		return new Errors();
	}

	public ExperimentDescription getDescription() {
		Map<String, Object> config2 = getConfig();
		if (config2.isEmpty()) {
			return new ExperimentDescription("Can not load config", this);
		}
		if (config2.containsKey("description")) {
			return new ExperimentDescription(config2.get("description").toString(), this);
		}
		return new ExperimentDescription("None", this);
	}

	@Override
	public String toString() {
		File file = new File(this.path);
		ArrayList<String> sm = new ArrayList<>();
		while (true) {

			sm.add(file.getName());
			file = file.getParentFile();
			if (file == null) {
				break;
			}
			if (file.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
				break;
			}
		}
		Collections.reverse(sm);
		return sm.stream().collect(Collectors.joining("/"));
	}

	

	public String getDataSet() {
		Object object = getConfig().get("dataset");
		if (object == null) {
			object = "none";
		}

		String string = object.toString();
		if (object instanceof Map) {
			string = string.substring(1, string.length() - 1);
		}
		return string;
	}

	public String getPrimaryMetric() {
		Object object = getConfig().get("primary_metric");
		if (object == null) {
			object = "none";
		}
		Object aMetric = getConfig().get("experiment_result");
		if (aMetric != null) {
			return aMetric.toString();
		}
		return (String) object;
	}

	public IPath getPath() {
		return new Path(path);
	}

	public ModelEvaluationSpec createModelSpec() {
		return new ModelEvaluationSpec(this.hasSeeds(), this.hasStages(), this.hasFolds());
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getDataSets() {
		ArrayList<String> datasets = new ArrayList<>();
		datasets.add("train");
		datasets.add("validation");
		if (getConfig().containsKey("testSplit")) {
			datasets.add("holdout");
		}
		Object object = getConfig().get("datasets");
		LinkedHashSet<String> existing = new LinkedHashSet<>();
		if (object instanceof Map) {
			Set<String> keySet = ((Map<String, ?>) object).keySet();
			existing.addAll(keySet);
			datasets.addAll(keySet);
		}
		try {
			ArrayList<BasicDataSetDesc> dataSets2 = ProjectManager.getInstance().getProject(this).getDataSets();
			dataSets2.forEach(v -> {
				if (!existing.contains(v.name)) {
					datasets.add(v.name);
				}
			});
		} catch (Exception e) {
			DSIDEUIPlugin.log(e);
		}
		return datasets;
	}

	public boolean hasSeeds() {
		Object orDefault2 = this.getConfig().getOrDefault("num_seeds", 1);
		if (orDefault2 instanceof Number) {
			Number orDefault = (Number) orDefault2;
			return orDefault.intValue() > 1;
		}
		return false;
	}

	public boolean hasStages() {
		List<?> orDefault = (List<?>) this.getConfig().getOrDefault("stages", 5);
		return orDefault.size() > 1;
	}

	public boolean hasFolds() {
		Number orDefault = (Number) this.getConfig().getOrDefault("folds_count", 5);
		return orDefault.intValue() > 1;
	}

	public ExperimentResults getSummary() {
		File summary = new File(this.path, "summary.yaml");
		if (summary.exists()) {
			ExperimentResults experimentResults = new ExperimentResults("summary", summary.getAbsolutePath());
			return experimentResults;
		}
		return null;
	}

	public void invalidateConfig() {
		this._score=null;
		this.config = null;
	}

	public boolean hasSplits() {
		return false;
	}
	
	public String getPathString() {
		return path;
	}

}
