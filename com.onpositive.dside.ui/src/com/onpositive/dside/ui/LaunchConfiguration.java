package com.onpositive.dside.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.onpositive.datasets.visualisation.ui.views.EnumRealmProvider;
import com.onpositive.dside.tasks.IServerTask;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.IExperimentExecutionListener;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/launch.dlf")
public class LaunchConfiguration implements IServerTask<Object> {

	public LaunchConfiguration() {
	}
	
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
	@Caption("Initialize data splits from scrach")
	protected boolean cleanSplits=true;

	public boolean isCleanSplits() {
		return cleanSplits;
	}

	public void setCleanSplits(boolean cleanSplits) {
		this.cleanSplits = cleanSplits;
	}

	@Range(min = 1, max = 16)
	int numGpus = 1;

	@Range(min = 1, max = 8)
	int gpusPerNet = 1;

	boolean allowResume;

	boolean onlyReports;

	boolean launchTasks;

	boolean fitFromScratch;
	
	boolean debug;
	
	@Caption("Please select fold selection strategy")
	@RealmProvider(EnumRealmProvider.class)
	@Required
	FoldSelectionStrategy folds=FoldSelectionStrategy.ALL;
	
	public boolean getShowFolds() {
		return folds==FoldSelectionStrategy.MANUAL;
	}
	
	ArrayList<Integer>folds_numbers=new ArrayList<>();
	
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

	@Override
	public Class<Object> resultClass() {
		return Object.class;
	}

	@Override
	public org.eclipse.core.resources.IProject[] getProjects() {
		ArrayList<org.eclipse.core.resources.IProject>p=new ArrayList<>();
		for(Experiment e:experiment) {
			IPath path = e.getPath();
			IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			if (fileForLocation!=null) {
				p.add(fileForLocation.getProject());
			}
		}
		return p.toArray(new org.eclipse.core.resources.IProject[p.size()]);
	}

	@Override
	public String getPreferredLaunchDelegate() {
		return "com.onpositive.dside.musket.launch";
	}

	
}
