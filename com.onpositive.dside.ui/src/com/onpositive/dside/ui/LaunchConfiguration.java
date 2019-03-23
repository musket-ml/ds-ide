package com.onpositive.dside.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.IExperimentExecutionListener;
import com.onpositive.musket_core.IHasAfterCompletionTasks;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Range;

@Display("dlf/launch.dlf")
public class LaunchConfiguration implements IHasAfterCompletionTasks {

	public LaunchConfiguration(Collection<Object> collection) {
		collection.forEach(e -> {
			experiment.add((Experiment) e);
		});
	}

	protected List<Experiment> experiment = new ArrayList<Experiment>();

	@Range(min = 1, max = 100)
	int numWorkers = 1;

	public int getNumWorkers() {
		return numWorkers;
	}

	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	public int getNumGpus() {
		return numGpus;
	}

	public void setNumGpus(int numGpus) {
		this.numGpus = numGpus;
	}

	public int getGpusPerNet() {
		return gpusPerNet;
	}

	public void setGpusPerNet(int gpusPerNet) {
		this.gpusPerNet = gpusPerNet;
	}

	@Range(min = 1, max = 16)
	int numGpus = 1;

	@Range(min = 1, max = 8)
	int gpusPerNet = 1;

	boolean allowResume;

	boolean onlyReports;

	boolean launchTasks;

	boolean fitFromScratch;

	public boolean isLaunchTasks() {
		return launchTasks;
	}

	public void setLaunchTasks(boolean launchTasks) {
		this.launchTasks = launchTasks;
	}

	public boolean isOnlyReports() {
		return onlyReports;
	}

	public void setOnlyReports(boolean onlyReports) {
		this.onlyReports = onlyReports;
	}

	public boolean isAllowResume() {
		return allowResume;
	}

	public void setAllowResume(boolean allowResume) {
		this.allowResume = allowResume;
	}

	public List<String> getExperiments() {
		return experiment.stream().map(x -> x.getPath().toPortableString()).collect(Collectors.toList());
	}

	public void setExperiments(List<String> strings) {

	}

	static ArrayList<IExperimentExecutionListener> listeners = new ArrayList<>();

	public static boolean addListener(IExperimentExecutionListener e) {
		return listeners.add(e);
	}

	public static boolean removeListener(Object o) {
		return listeners.remove(o);
	}

	@Override
	public String toString() {
		return "Perform " + getExperiments();
	}

	@Override
	public void afterCompletion(Object result) {
		org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				
				experiment.forEach(e -> {
					ExperimentsView.open(e);
					listeners.forEach(l->l.complete(e));
				});
			}
		});
	}
}
