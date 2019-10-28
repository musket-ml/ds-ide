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

import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.ui.introspection.IIntrospector;
import com.onpositive.dside.ui.introspection.ShellIntrospector;
import com.onpositive.yamledit.introspection.InstrospectedFeature;
import com.onpositive.yamledit.introspection.InstrospectionResult;
import com.onpositive.yamledit.io.YamlIO;
import com.onpositive.yamledit.project.IProjectContext;

public class ProjectWrapper {
	
	protected IIntrospector projectIntrospector;

	public ProjectWrapper(String projectPath) {
		this.path = projectPath;
		String absolutePath = projectMetaPath();
		synchronized (ProjectWrapper.this) {
			InstrospectionResult result = YamlIO.loadFromFile(new File(absolutePath), InstrospectionResult.class);
			if (result != null) {
				this.refreshed(result);
			}
		}
		projectIntrospector = createIntrospector();
	}

	protected IIntrospector createIntrospector() {
		return new ShellIntrospector();
	}

	protected String path;

	protected ArrayList<Runnable> requests = new ArrayList<>();
	protected ArrayList<Runnable> listeners = new ArrayList<>();

	private InstrospectionResult details = new InstrospectionResult();

	protected Object mon = new Object();

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
	
	public IProjectContext getProjectContext()	{
		return new IProjectContext() {
			
			@Override
			public File[] getAdditionalFiles() {
				return new File[] {new File(path, "common.yaml")};
			}
		};
	}
	
	public static class BasicDataSetDesc {

		public String name;
		public String kind;
		public String origin;
		public String functionName;

		@Override
		public String toString() {
			return name;
		}
	}

	public ArrayList<BasicDataSetDesc> getDataSets() {

		try {
			ArrayList<FunctionDeclaration> introspectModules = introspectModules();
			LinkedHashMap<String, FunctionDeclaration> maps = new LinkedHashMap<>();
			introspectModules.forEach(m -> maps.put(m.name, m));
			FileReader fileReader = new FileReader(new File(path, "common.yaml"));
			Object loadAs = YamlIO.loadAs(fileReader, Object.class);
			if (loadAs instanceof Map) {
				Map<String, Object> m = (Map<String, Object>) loadAs;
				Object object = m.get("datasets");
				if (object instanceof Map) {
					Map<String, Object> vls = (Map<String, Object>) object;
					ArrayList<BasicDataSetDesc> result = new ArrayList<>();
					vls.keySet().forEach(v -> {
						Object rs = vls.get(v);
						BasicDataSetDesc ds = new BasicDataSetDesc();
						ds.name = v;
						if (rs instanceof Map) {
							Map<String, Object> d = (Map<String, Object>) rs;
							String decl = d.keySet().iterator().next();
							FunctionDeclaration functionDeclaration = maps.get(decl);
							if (functionDeclaration != null) {
								ds.functionName=functionDeclaration.name;
								functionDeclaration.annotations.forEach(a -> {
									if (a.name.indexOf("dataset_provider") != -1) {
										if (!a.namedParameters.isEmpty()) {
											Object object2 = a.namedParameters.get("origin");
											Object object3 = a.namedParameters.get("kind");
											if (object2 != null) {
												ds.origin = object2.toString();
											}
											if (object3 != null) {
												ds.kind = object3.toString();
											}
											if (ds.origin.charAt(0) == '"') {
												ds.origin = ds.origin.substring(1, ds.origin.length() - 1);
											}
										}
									}
								});
								
							}
						}
						result.add(ds);
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

	protected static class ParsedAnnotation {
		String name;
		ArrayList<String> unnamedParameters = new ArrayList<>();
		Map<String, Object> namedParameters = new LinkedHashMap<String, Object>();
	}

	protected static class FunctionDeclaration {
		ArrayList<ParsedAnnotation> annotations = new ArrayList<>();
		String name;
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

	public synchronized void refresh(Runnable request) {
		if (request != null) {
			this.requests.add(request);
		}
		Job create = Job.create("Refreshing project meta", new IJobFunction() {

			@Override
			public IStatus run(IProgressMonitor monitor) {

				String pythonPath = null;
				String absolutePath = projectMetaPath();
				try {
					IContainer[] findContainersForLocation = ResourcesPlugin.getWorkspace().getRoot()
							.findContainersForLocation(new Path(path));
					if (findContainersForLocation != null&&findContainersForLocation.length>0) {
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
	
	

	public void innerIntrospect(String pythonPath, String absolutePath) {
		synchronized (mon) {
			InstrospectionResult introspect = projectIntrospector.introspect(path, pythonPath, absolutePath);
			if (introspect!=null) {
				refreshed(introspect);
			}
		}

	}
}
