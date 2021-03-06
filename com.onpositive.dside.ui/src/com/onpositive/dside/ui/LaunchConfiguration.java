package com.onpositive.dside.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.onpositive.datasets.visualisation.ui.views.EnumRealmProvider;
import com.onpositive.dside.tasks.IServerTask;
import com.onpositive.dside.ui.views.ExperimentsView;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.IExperimentExecutionListener;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/launch.dlf")
public class LaunchConfiguration implements IServerTask<Object>, IHasName {

	protected List<String> experiments = new ArrayList<>();
	
	@Range(min = 1, max = 100)
	protected int numWorkers = 1;

	@Range(min = 1, max = 16)
	protected int numGpus = 1;

	@Range(min = 1, max = 8)
	protected int gpusPerNet = 1;

	protected boolean allowResume;

	protected boolean onlyReports;

	protected boolean launchTasks;

	protected boolean fitFromScratch = true;

	protected boolean debug;

	@Caption("Please select fold selection strategy")
	@RealmProvider(EnumRealmProvider.class)
	@Required
	FoldSelectionStrategy folds=FoldSelectionStrategy.ALL;

	ArrayList<Integer>folds_numbers=new ArrayList<>();
	
	public LaunchConfiguration() {
	}

	public LaunchConfiguration(Collection<Experiment> collection) {
		experiments.addAll(collection.stream().map(item -> item.getPathString()).collect(Collectors.toList()));
	}

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
	@Caption("Initialize data splits from scrach")
	protected boolean cleanSplits=true;

	public boolean isCleanSplits() {
		return cleanSplits;
	}

	public void setCleanSplits(boolean cleanSplits) {
		this.cleanSplits = cleanSplits;
	}

	public boolean getShowFolds() {
		return folds==FoldSelectionStrategy.MANUAL;
	}
	
	public String getFolds() {
		return folds_numbers.stream().map(x->x.toString()).collect(Collectors.joining(","));
	}
	
	public void setFolds(String s) {
		
	}
	
	public boolean save() {return true;}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

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
		return experiments;
	}

	public void setExperiments(List<String> experiments) {
		this.experiments = experiments;
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
				
				experiments.forEach(path -> {
					ExperimentsView.open(path);
					listeners.forEach(l->l.complete(path));
				});
			}
		});
	}

	@Override
	public Class<Object> resultClass() {
		return Object.class;
	}

	@Override
	public org.eclipse.core.resources.IProject[] getProjects() {
		ArrayList<org.eclipse.core.resources.IProject>p=new ArrayList<>();
		for(String path:experiments) {
			IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(path));
			if (fileForLocation!=null) {
				p.add(fileForLocation.getProject());
			}
		}
		return p.toArray(new org.eclipse.core.resources.IProject[p.size()]);
	}

	@Override
	public String getPreferredLaunchConfigType() {
		return "com.onpositive.dside.musket.launch";
	}

	@Override
	public String getName() {
		if (!experiments.isEmpty()) {
			Experiment experiment = new Experiment(experiments.get(0));
			String projectPath = new Path(experiment.getProjectPath()).lastSegment();
			String name = projectPath + " " + experiment.getPath().lastSegment();
			if (experiments.size() > 1) {
				name += " ...";
			}
			return name;
		}
		return "<experiment>";
	}

	public boolean isFitFromScratch() {
		return fitFromScratch;
	}

	public void setFitFromScratch(boolean fitFromScratch) {
		this.fitFromScratch = fitFromScratch;
	}
	
}
