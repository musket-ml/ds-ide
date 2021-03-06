package com.onpositive.musket_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
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
import org.python.pydev.ast.interpreter_managers.InterpreterManagersAPI;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.debug.ui.launching.FileOrResource;
import org.python.pydev.debug.ui.launching.InvalidRunException;
import org.python.pydev.debug.ui.launching.LaunchShortcut;
import org.python.pydev.debug.ui.launching.PythonRunnerConfig;

import com.onpositive.dside.tasks.InternalMusketLaunchShortcut;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.IMusketConstants;
import com.onpositive.dside.ui.introspection.IIntrospector;
import com.onpositive.dside.ui.introspection.ShellIntrospector;
import com.onpositive.python.command.IPythonPathProvider;
import com.onpositive.yamledit.introspection.InstrospectedFeature;
import com.onpositive.yamledit.introspection.InstrospectionResult;
import com.onpositive.yamledit.io.YamlIO;
import com.onpositive.yamledit.project.IProjectContext;

public class ProjectWrapper implements IPythonPathProvider {
	
	public static class BasicDataSetDesc {

		public String functionName;
		public String kind;
		public String name;
		public String origin;

		@Override
		public String toString() {
			return name;
		}
	}

	protected static class FunctionDeclaration {
		ArrayList<ParsedAnnotation> annotations = new ArrayList<>();
		String name;
	}

	protected static class ParsedAnnotation {
		String name;
		Map<String, Object> namedParameters = new LinkedHashMap<String, Object>();
		ArrayList<String> unnamedParameters = new ArrayList<>();
	}

	private InstrospectionResult details = new InstrospectionResult();

	protected ArrayList<Runnable> listeners = new ArrayList<>();
	protected Object mon = new Object();

	protected String path;

	protected IIntrospector projectIntrospector;

	protected ArrayList<Runnable> requests = new ArrayList<>();

	public ProjectWrapper(String projectPath) {
		this.path = projectPath;
		String absolutePath = projectMetaPath();
		Job introspectJob = new Job("Introspecting config for " + path) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				synchronized (ProjectWrapper.this) {
					InstrospectionResult result = YamlIO.loadFromFile(new File(absolutePath), InstrospectionResult.class);
					if (result != null) {
						ProjectWrapper.this.refreshed(result);
					}
				}
				return Status.OK_STATUS;
			}
			
		};
		introspectJob.schedule();
		projectIntrospector = createIntrospector();
	}

	public void addRefreshListener(Runnable r) {
		this.listeners.add(r);
	}

	protected IIntrospector createIntrospector() {
		return new ShellIntrospector();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<BasicDataSetDesc> getDataSets() {

		try {
			ArrayList<FunctionDeclaration> introspectModules = introspectModules();
			LinkedHashMap<String, FunctionDeclaration> maps = new LinkedHashMap<>();
			introspectModules.forEach(m -> maps.put(m.name, m));
			FileReader fileReader = new FileReader(new File(path, IMusketConstants.COMMON_CONFIG_NAME));
			Object loadAs = YamlIO.loadAs(fileReader, Object.class);
			if (loadAs instanceof Map) {
				Map<String, Object> loadedMap = (Map<String, Object>) loadAs;
				Object object = loadedMap.get("datasets");
				if (object instanceof Map) {
					Map<String, Object> vls = (Map<String, Object>) object;
					ArrayList<BasicDataSetDesc> result = new ArrayList<>();
					vls.keySet().forEach(key -> {
						Object rs = vls.get(key);
						BasicDataSetDesc dataSetDesc = new BasicDataSetDesc();
						dataSetDesc.name = key;
						if (rs instanceof Map) {
							Map<String, Object> d = (Map<String, Object>) rs;
							String decl = d.keySet().iterator().next();
							FunctionDeclaration functionDeclaration = maps.get(decl);
							if (functionDeclaration != null) {
								dataSetDesc.functionName=functionDeclaration.name;
								functionDeclaration.annotations.forEach(a -> {
									if (a.name.indexOf("dataset_provider") != -1) {
										if (!a.namedParameters.isEmpty()) {
											Object object2 = a.namedParameters.get("origin");
											Object object3 = a.namedParameters.get("kind");
											if (object2 != null) {
												dataSetDesc.origin = object2.toString();
											}
											if (object3 != null) {
												dataSetDesc.kind = object3.toString();
											}
											if (dataSetDesc.origin.charAt(0) == '"') {
												dataSetDesc.origin = dataSetDesc.origin.substring(1, dataSetDesc.origin.length() - 1);
											}
										}
									}
								});
								
							}
						}
						result.add(dataSetDesc);
					});
					return result;
				}
			}
			fileReader.close();

		} catch (FileNotFoundException e) {
			return new ArrayList<>();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return new ArrayList<>();
	}
	
	public InstrospectionResult getDetails() {
		return details;
	}

	private File getMetaDir() {
		File file = new File(this.path, ".meta");
		file.mkdirs();
		return file;
	}

	public String getPath() {
		return this.path;
	}

	public IProjectContext getProjectContext()	{
		return new IProjectContext() {
			
			@Override
			public File[] getAdditionalFiles() {
				return new File[] {new File(path, IMusketConstants.COMMON_CONFIG_NAME)};
			}
		};
	}

	public PyInfo getPythonPath() {
		try {
			IContainer[] foundContainers = ResourcesPlugin.getWorkspace().getRoot()
					.findContainersForLocation(new Path(this.path));
			if (foundContainers != null&&foundContainers.length>0) {
				IProject project = foundContainers[0].getProject();
				IFolder experimentsFolder = project.getFolder(IMusketConstants.MUSKET_EXPERIMENTS_FOLDER);
				if (experimentsFolder.exists()) {
					return tryGetPyInfo(project, experimentsFolder);
				} else {
					return tryGetPyInfo(project, foundContainers[0]);
				}
			}

		} catch (CoreException | InvalidRunException | MisconfigurationException e1) {
			DSIDEUIPlugin.log(e1);
		}
		return null;
	}

	protected PyInfo tryGetPyInfo(IProject project, IContainer baseFolder)
			throws CoreException, InvalidRunException, MisconfigurationException {
		String pythonPath;
		LaunchShortcut launchShortCut = new InternalMusketLaunchShortcut(new IProject[] { project }, "org.python.pydev.debug.musketLaunchConfigurationType");
		ILaunchConfiguration defaultLaunchConfiguration = launchShortCut
				.createDefaultLaunchConfiguration(
						new FileOrResource[] { new FileOrResource(baseFolder) });
		PythonRunnerConfig pythonRunner = new PythonRunnerConfig(defaultLaunchConfiguration,
				"run", "run");
		String pythonpathFromConfiguration = PythonRunnerConfig.getPythonpathFromConfiguration(
				defaultLaunchConfiguration, InterpreterManagersAPI.getPythonInterpreterManager());
		pythonPath = pythonpathFromConfiguration;
		return new PyInfo(pythonPath, pythonRunner.interpreter.toFile().getAbsolutePath());
	}

	public List<InstrospectedFeature> getTasks() {
		return details.getFeatures().stream().filter(x -> x.getKind().equals("task")).collect(Collectors.toList());
	}
	
	public void innerIntrospect(PyInfo pythonPath, String absolutePath) {
		synchronized (mon) {
			if (pythonPath != null) {
				InstrospectionResult introspect = projectIntrospector.introspect(path, pythonPath, absolutePath);
				if (introspect!=null) {
					refreshed(introspect);
				}
			}
		}
	}

	public ArrayList<FunctionDeclaration> introspectModules() {
		File fl = new File(path, "modules");

		ArrayList<FunctionDeclaration> declarations = new ArrayList<>();
		for (File q : fl.listFiles()) {
			if (q.isFile() && q.getName().endsWith(".py")) {
				try {
					List<String> readAllLines = Files.readAllLines(q.toPath());
					ArrayList<ParsedAnnotation> annotations = new ArrayList<>();
					for (String s : readAllLines) {
						s = s.trim();
						if (s.isEmpty()) {
							continue;
						}
						if (s.startsWith("@")) {

							String annotationName = s.substring(1);
							int indexOf = annotationName.indexOf('(');
							ParsedAnnotation parsedAnnotation = new ParsedAnnotation();
							if (indexOf != -1) {
								int indexOf2 = annotationName.indexOf(')');
								if (indexOf2 != -1) {
									String parList = annotationName.substring(indexOf + 1, indexOf2);
									System.out.println(parList);
									String[] split = parList.split(",");
									for (String par : split) {
										int indexOf3 = par.indexOf("=");
										if (indexOf3 != -1) {
											String pName = par.substring(0, indexOf3).trim();
											String pValue = par.substring(indexOf3 + 1).trim();
											parsedAnnotation.namedParameters.put(pName, pValue);
										} else {
											parsedAnnotation.unnamedParameters.add(par.trim());
										}
									}
								}
								annotationName = annotationName.substring(0, indexOf);

							}
							parsedAnnotation.name = annotationName;
							annotations.add(parsedAnnotation);
						} else if (s.startsWith("def")) {
							s = s.substring(3).trim();
							int ps = s.indexOf('(');
							if (ps != -1) {
								String name = s.substring(0, ps).trim();
								FunctionDeclaration decl = new FunctionDeclaration();
								decl.name = name;
								decl.annotations.addAll(annotations);
								declarations.add(decl);
							}
						} else {
							annotations.clear();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return declarations;
	}

	private String projectMetaPath() {
		return new File(this.getMetaDir(), "meta.yaml").getAbsolutePath();
	}

	public synchronized void refresh(Runnable request) {
		if (request != null) {
			this.requests.add(request);
		}
		Job create = Job.create("Refreshing project meta", new IJobFunction() {

			@Override
			public IStatus run(IProgressMonitor monitor) {
				String absolutePath = projectMetaPath();
				PyInfo pythonPath = ProjectWrapper.this.getPythonPath();
				if (pythonPath != null) {
					innerIntrospect(pythonPath, absolutePath);
				}
				return Status.OK_STATUS;
			}
		});
		create.schedule();
	}

	protected void refreshed(InstrospectionResult details) {
		try {
			if (details == null) {
				return;
			}
			this.details = details;
			for (Runnable request : requests) {
				request.run();
			}
			for (Runnable listener : new ArrayList<>(listeners)) {
				listener.run();
			}
		} finally {
			requests.clear();
		}
	}

	public void removeRefreshListener(Runnable r) {
		this.listeners.remove(r);
	}

	public void setDetails(InstrospectionResult details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "ProjectWrapper [path=" + path + "]";
	}
}
