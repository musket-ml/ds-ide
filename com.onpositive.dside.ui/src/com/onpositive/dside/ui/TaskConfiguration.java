package com.onpositive.dside.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.IExperimentExecutionListener;
import com.onpositive.musket_core.IHasAfterCompletionTasks;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Range;

@Display("dlf/task.dlf")
public class TaskConfiguration implements IHasAfterCompletionTasks {

	public TaskConfiguration(Collection<Object> collection) {
		collection.forEach(e -> {
			experiment.add((Experiment) e);
		});
	}

	protected List<Experiment> experiment = new ArrayList<Experiment>();

	protected String tasks;

	public String getTasks() {
		return tasks;
	}

	public void setTasks(String tasks) {
		this.tasks = tasks;
	}

	public Map<String, Object> getTaskArgs() {
		return taskArgs;
	}

	public void setTaskArgs(Map<String, Object> taskArgs) {
		this.taskArgs = taskArgs;
	}

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

	protected Map<String, Object> taskArgs = new LinkedHashMap<>();

	boolean fitFromScratch;

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
				ArrayList<Object> rrr = (ArrayList<Object>) result;
				for (Object o : rrr) {
					Map<String, Object> mmm = (Map<String, Object>) o;
					if (mmm.containsKey("results")) {
						Object object = mmm.get("results");
						if (object instanceof String) {
							File fl = new File(object.toString());
							File[] listFiles = fl.listFiles();
							if (listFiles.length < 5) {
								for (File f : listFiles) {
									if (f.isFile()) {
										IFile[] findFilesForLocation = ResourcesPlugin.getWorkspace().getRoot()
												.findFilesForLocation(new Path(f.getAbsolutePath()));
										for (IFile fa : findFilesForLocation) {
											try {
												fa.refreshLocal(IFile.DEPTH_INFINITE, new NullProgressMonitor());
												FileEditorInput input = new FileEditorInput(fa);
												IEditorDescriptor defaultEditor = PlatformUI.getWorkbench()
														.getEditorRegistry().getDefaultEditor(fa.getName());
												if (defaultEditor != null) {
													try {
														PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
																.openEditor(input, defaultEditor.getId());
													} catch (PartInitException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}
											} catch (CoreException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
											

										}
									}
								}
							}
							System.out.println(fl);
						}
					}
					System.out.println(mmm);
				}
			}
		});
	}
}
