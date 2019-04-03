package com.onpositive.musket_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.yaml.snakeyaml.Yaml;

import com.onpositive.dside.ui.ModelEvaluationSpec;
import com.onpositive.semantic.model.api.property.java.annotations.Image;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.semantic.model.api.realm.Realm;

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
		while (true) {
			if (file.getParentFile() == null) {
				break;
			}
			file = file.getParentFile();
			if (file.getName().equals("experiments")) {
				return file.getParentFile().getAbsolutePath();
			}
		}
		return file.getAbsolutePath();
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
				Object load = new Yaml().load(fileReader);
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
		File file = new File(path, "config.yaml");
		try {
			FileReader fileReader = new FileReader(file);
			try {
				Map load = (Map) new Yaml().load(fileReader);
				this.config = load;
			} finally {
				fileReader.close();
			}
		} catch (Exception e) {
			return new HashMap<>();
		}
		return config;
	}

	public Errors getErrors() {
		File file = new File(path, "error.yaml");
		if (file.exists()) {
			try {
				FileReader fileReader = new FileReader(file);
				try {
					return new Yaml().loadAs(fileReader, Errors.class);
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

	public ArrayList<ExperimentLogs> logs() {
		ArrayList<ExperimentLogs> r = new ArrayList<>();
		this.gatherLogs("", this.path, r);
		return r;
	}

	public ArrayList<ExperimentResults> results() {
		ArrayList<ExperimentResults> r = new ArrayList<>();
		this.gatherResults("", this.path, r);
		return r;
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
			if (file.getName().equals("experiments")) {
				break;
			}
		}
		Collections.reverse(sm);
		return sm.stream().collect(Collectors.joining("/"));
	}

	private void gatherResults(String baseName, String path2, ArrayList<ExperimentResults> r) {
		File[] listFiles = new java.io.File(path2).listFiles();
		for (File f : listFiles) {
			if (f.getName().equals("summary.yaml")) {
				r.add(new ExperimentResults(baseName, f.getAbsolutePath()));
			} else {
				if (f.getName().startsWith("trial")) {
					gatherResults("trial: " + f.getName(), f.getAbsolutePath(), r);
				}
				try {
					int parseInt = Integer.parseInt(f.getName());
					gatherResults(baseName + " split:" + f.getName(), f.getAbsolutePath(), r);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private void gatherLogs(String baseName, String path2, ArrayList<ExperimentLogs> r) {
		File[] listFiles = new java.io.File(path2).listFiles();
		for (File f : listFiles) {
			if (f.getName().equals("metrics") && f.isDirectory()) {
				for (File f1 : f.listFiles()) {
					String name = f1.getName();
					int indexOf = name.indexOf('-');
					if (indexOf != -1) {
						String[] split = name.substring(indexOf + 1).split("\\.");
						ExperimentLogs rs = new ExperimentLogs(baseName + " fold:" + split[0] + " stage:" + split[1],
								f1.getAbsolutePath());
						r.add(rs);
					}
				}
			} else {
				if (f.getName().startsWith("trial")) {
					gatherLogs("trial: " + f.getName(), f.getAbsolutePath(), r);
				}
				try {
					int parseInt = Integer.parseInt(f.getName());
					gatherLogs(baseName + " split:" + f.getName(), f.getAbsolutePath(), r);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
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
		Object aMetric = getConfig().get("aggregation_metric");
		if (aMetric != null) {
			return aMetric.toString();
		}
		return (String) object;
	}

	public boolean delete() {
		IContainer[] findContainersForLocation = ResourcesPlugin.getWorkspace().getRoot()
				.findContainersForLocation(new Path(this.path));
		for (IContainer c : findContainersForLocation) {
			try {
				c.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				return false;
			}
		}
		return true;
	}

	public Experiment duplicate(String value) {
		IContainer[] findContainersForLocation = ResourcesPlugin.getWorkspace().getRoot()
				.findContainersForLocation(new Path(this.path));
		for (IContainer c : findContainersForLocation) {
			IFile file = c.getFile(new Path("config.yaml"));
			if (file.exists()) {
				IContainer parent = c.getParent();
				IFolder folder = parent.getFolder(new Path(value));
				if (!folder.exists()) {
					try {
						folder.create(true, true, new NullProgressMonitor());
						IPath append = folder.getFullPath().append("config.yaml");
						file.copy(append, true, new NullProgressMonitor());
						IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(append);
						String portableString = file2.getParent().getLocation().toPortableString();
						return new Experiment(portableString);
					} catch (CoreException e) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
					}
				} else {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
							"Experiment with this name already exist at:" + parent.getFullPath().toPortableString());
				}
			}
		}
		return null;
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
		if (object instanceof Map) {
			datasets.addAll(((Map) object).keySet());
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
		List orDefault = (List) this.getConfig().getOrDefault("stages", 5);
		return orDefault.size() > 1;
	}

	public boolean hasFolds() {
		Number orDefault = (Number) this.getConfig().getOrDefault("folds_count", 5);
		return orDefault.intValue() > 1;
	}
}
