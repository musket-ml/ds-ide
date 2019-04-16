package com.onpositive.musket_core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobFunction;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;
import org.python.pydev.ast.interpreter_managers.InterpreterManagersAPI;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.debug.ui.launching.FileOrResource;
import org.python.pydev.debug.ui.launching.InvalidRunException;
import org.python.pydev.debug.ui.launching.LaunchShortcut;
import org.python.pydev.debug.ui.launching.PythonRunnerConfig;
import org.yaml.snakeyaml.Yaml;

import com.onpositive.dside.dto.PythonError;
import com.onpositive.dside.dto.introspection.InstrospectedFeature;
import com.onpositive.dside.dto.introspection.InstrospectionResult;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class ProjectWrapper {

	public ProjectWrapper(String projectPath) {
		this.path = projectPath;
		String absolutePath = projectMetaPath();
		synchronized (ProjectWrapper.this) {
			if (new File(absolutePath).exists()) {
				FileReader fileReader;
				try {
					fileReader = new FileReader(absolutePath);
					try {
						InstrospectionResult loadAs = new Yaml().loadAs(fileReader, InstrospectionResult.class);
						this.refreshed(loadAs);
					} finally {
						fileReader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected String path;

	protected ArrayList<Runnable> requests = new ArrayList<>();
	protected ArrayList<Runnable> listeners = new ArrayList<>();

	private InstrospectionResult details = new InstrospectionResult();

	public InstrospectionResult getDetails() {
		return details;
	}

	public void addRefreshListener(Runnable r) {
		this.listeners.add(r);
	}

	public void removeRefreshListener(Runnable r) {
		this.listeners.remove(r);
	}

	public void setDetails(InstrospectionResult details) {
		this.details = details;
	}

	public synchronized void refresh(Runnable r) {
		if (r != null) {
			this.requests.add(r);
		}
		Job create = Job.create("Refreshing project meta", new IJobFunction() {

			@Override
			public IStatus run(IProgressMonitor monitor) {

				String pythonPath = null;
				String absolutePath = projectMetaPath();
				try {
					IContainer[] findContainersForLocation = ResourcesPlugin.getWorkspace().getRoot()
							.findContainersForLocation(new Path(path));
					if (findContainersForLocation != null) {
						IProject project = findContainersForLocation[0].getProject();
						LaunchShortcut launchShortCut = TaskManager.launchShortCut(new IProject[] { project });
						ILaunchConfiguration createDefaultLaunchConfiguration = launchShortCut
								.createDefaultLaunchConfiguration(
										new FileOrResource[] { new FileOrResource(project.getFolder("experiments")) });
						PythonRunnerConfig pythonRunner = new PythonRunnerConfig(createDefaultLaunchConfiguration,
								"run", "run");
						String pythonpathFromConfiguration = pythonRunner.getPythonpathFromConfiguration(
								createDefaultLaunchConfiguration, InterpreterManagersAPI.getPythonInterpreterManager());
						pythonPath = pythonpathFromConfiguration;
					}

				} catch (CoreException | InvalidRunException | MisconfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				innerIntrospect(pythonPath, absolutePath);
				return Status.OK_STATUS;
			}

		});
		create.schedule();

	}

	private String projectMetaPath() {
		return new File(this.getMetaDir(), "meta.yaml").getAbsolutePath();
	}

	private File getMetaDir() {
		File file = new File(this.path, ".meta");
		file.mkdirs();
		return file;
	}

	protected void refreshed(InstrospectionResult details) {
		try {
			if (details == null) {
				return;
			}
			this.details = details;
			for (Runnable r : requests) {
				r.run();
			}
			for (Runnable r : new ArrayList<>(listeners)) {
				r.run();
			}
		} finally {
			requests.clear();
		}
	}

	public List<InstrospectedFeature> getTasks() {
		return details.getFeatures().stream().filter(x -> x.getKind().equals("task")).collect(Collectors.toList());
	}

	public String getPath() {
		return this.path;
	}

	protected Object mon = new Object();

	public void innerIntrospect(String pythonPath, String absolutePath) {
		synchronized (mon) {
			ProcessBuilder command = new ProcessBuilder().command("python", "-m", "musket_core.inspectProject",
					"--project", path, "--out", absolutePath);
			try {
				command.environment().putAll(System.getenv());
				if (pythonPath != null) {
					command.environment().put("PYTHONPATH", pythonPath);
				}
				File file = new File(getMetaDir() + "/error.log");
				command.redirectError(file);
				command.redirectOutput(new File(getMetaDir() + "/output.log"));
				int waitFor = command.start().waitFor();
				if (waitFor != 0) {
					List<String> readAllLines = Files.readAllLines(file.toPath());
					PythonError pythonError = new PythonError(readAllLines);
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							boolean createObject = WidgetRegistry.createObject(new StackVisualizer(pythonError));
							if (createObject) {
								pythonError.open();
							}
						}
					});
					return;
				}
				FileReader fileReader = new FileReader(absolutePath);
				try {
					InstrospectionResult loadAs = new Yaml().loadAs(fileReader, InstrospectionResult.class);
					refreshed(loadAs);

				} finally {
					fileReader.close();
				}
				// ServerManager.perform(new IntrospectTask(this));
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}

	}
}
