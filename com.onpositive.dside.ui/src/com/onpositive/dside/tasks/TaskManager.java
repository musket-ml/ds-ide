package com.onpositive.dside.tasks;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.debug.core.Constants;
import org.python.pydev.debug.ui.launching.FileOrResource;
import org.python.pydev.debug.ui.launching.LaunchConfigurationCreator;
import org.python.pydev.debug.ui.launching.LaunchShortcut;
import org.python.pydev.plugin.nature.PythonNature;
import org.python.pydev.shared_core.callbacks.ICallback;
import org.python.pydev.shared_core.utils.ArrayUtils;
import org.python.pydev.shared_ui.EditorUtils;
import org.python.pydev.shared_ui.dialogs.ProjectSelectionDialog;
import org.python.pydev.shared_ui.utils.RunInUiThread;
import org.yaml.snakeyaml.Yaml;

public class TaskManager {

	static ArrayList<Runnable> onJobComplete = new ArrayList<>();

	public static void addJobListener(Runnable r) {
		onJobComplete.add(r);
	}

	public static void removeJobListener(Runnable r) {
		onJobComplete.remove(r);
	}

	static class TaskStatus {
		protected IServerTask<Object> task;
		protected Path path;
		protected boolean reported;

		public TaskStatus(IServerTask<Object> task, Path path) {
			super();
			this.task = task;
			this.path = path;
		}

		public void report(ILaunch launch) {
			if (reported) {
				return;
			}
			try {
				byte[] readAllBytes = Files.readAllBytes(path);
				Object loadAs = new Yaml().loadAs(new StringReader(new String(readAllBytes)), task.resultClass());
				task.afterCompletion(loadAs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.reported = true;
		}

	}

	static IdentityHashMap<ILaunch, TaskStatus> tasks = new IdentityHashMap<>();

	static {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		launchManager.addLaunchListener(new ILaunchesListener2() {

			

			private void perform(ILaunch l) {
				if (tasks.containsKey(l)) {
					TaskStatus taskStatus = tasks.get(l);
					if (l.isTerminated()) {
						taskStatus.report(l);
						tasks.remove(l);
					}
				}
			}

			

			@Override
			public void launchesRemoved(ILaunch[] launches) {
				for (ILaunch l:launches) {
					perform(l);
				}
			}

			@Override
			public void launchesAdded(ILaunch[] launches) {
				for (ILaunch l:launches) {
					perform(l);
				}
			}

			@Override
			public void launchesChanged(ILaunch[] launches) {
				for (ILaunch l:launches) {
					perform(l);
				}
			}

			@Override
			public void launchesTerminated(ILaunch[] launches) {
				for (ILaunch l:launches) {
					perform(l);
				}
			}

			
		});
	}

	public static void perform(IServerTask<?> task) {
		task.beforeStart();
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.console.ConsoleView");
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		IProject[] projects = task.getProject();

		LaunchShortcut launchShortcut = launchShortCut(projects);
		String dump = new Yaml().dump(task);
		try {
			Path absolutePath = null;
			Path absolutePath1 = null;
			Path absolutePath2 = Files.createTempFile("aaa", "result").toAbsolutePath();
			try {
				absolutePath = Files.createTempFile("aaa", "task").toAbsolutePath();
				Path write = Files.write(absolutePath, dump.getBytes());

				absolutePath1 = Files.createTempFile("aaa", ".py").toAbsolutePath();

				ArrayList<String> codeToRun = new ArrayList<>();
				codeToRun.add("import sys\n" + "from musket_core import tools,projects\n" + "import yaml\n"
						+ "import threading\n" + "from musket_core import utils\n" + "config = sys.argv[1]\n"
						+ "out = sys.argv[2]\n" + "def main():\n" + "        global config\n"
						+ "        with open(config,\"r\") as f:\n" + "            config=f.read()\n"
						+ "        config = config[1:].replace(\"!!com.onpositive\", \"!com.onpositive\")\n"
						+ "        obj = yaml.load(config)\n" + "        print(obj)\n"
						+ "        results = obj.perform(projects.Workspace(), tools.ProgressMonitor())\n"
						+ "        with open(out,\"w\") as f:\n" + "            f.write(yaml.dump(results))\n"
						+ "if __name__ == \"__main__\":"
						+ "    main()");
				Path write1 = Files.write(absolutePath1, codeToRun);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ILaunchConfigurationWorkingCopy createDefaultLaunchConfigurationWithoutSaving = launchShortcut
					.createDefaultLaunchConfigurationWithoutSaving(
							new FileOrResource[] { new FileOrResource(absolutePath1.toFile()) });
			HashSet<String> modes = new HashSet<>();
			modes.add("run");
			modes.add("debug");
			if (projects.length > 0) {
				createDefaultLaunchConfigurationWithoutSaving.setAttribute(Constants.ATTR_PROJECT,
						projects[0].getName());
			}
			createDefaultLaunchConfigurationWithoutSaving.setPreferredLaunchDelegate(modes,
					"org.python.pydev.debug.musketLaunchConfigurationType");
			ArrayList<String> args = new ArrayList<>();
			args.add(absolutePath.toString());
			args.add(absolutePath2.toString());
			createDefaultLaunchConfigurationWithoutSaving.setAttribute(Constants.ATTR_PROGRAM_ARGUMENTS,
					args.stream().collect(Collectors.joining(" ")));

			// createDefaultLaunchConfigurationWithoutSaving.setAttribute(Constants.ATTR_WORKING_DIRECTORY,
			// "D:/");
			HashMap<String, String> value = new HashMap<>();
			value.put("PYTHONUNBUFFERED", "1");
			createDefaultLaunchConfigurationWithoutSaving.setAttribute(DebugPlugin.ATTR_ENVIRONMENT, value);
			if (task.save()) {
				createDefaultLaunchConfigurationWithoutSaving.doSave();
			}
			ILaunch launch = createDefaultLaunchConfigurationWithoutSaving.launch(task.isDebug() ? "debug" : "run",
					new NullProgressMonitor());
			TaskStatus value2 = new TaskStatus((IServerTask<Object>) task, absolutePath2);
			if (launch.isTerminated()) {
				value2.report(launch);
			}
			tasks.put(launch, value2);
			task.afterStart(launch);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static LaunchShortcut launchShortCut(IProject[] projects) {
		LaunchShortcut launchShortcut = new LaunchShortcut() {

			@Override
			protected String getLaunchConfigurationType() {
				return "org.python.pydev.debug.musketLaunchConfigurationType";
			}

			public ILaunchConfigurationWorkingCopy createDefaultLaunchConfigurationWithoutSaving(
					FileOrResource[] resource) throws CoreException {
				IProject project;
				if (projects.length > 0) {
	                project=projects[0];
	            }
				 else {
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					List<IProject> projectsLst = ArrayUtils.filter(projects, new ICallback<Boolean, IProject>() {

						@Override
						public Boolean call(IProject arg) {
							IProject project = arg;
							try {
								return project.isOpen() && project.hasNature(PythonNature.PYTHON_NATURE_ID);
							} catch (CoreException e) {
								return false;
							}
						}
					});
					final Object[] found = new Object[1];
					if (projectsLst.size() == 0) {
						found[0] = new CoreException(new Status(IStatus.ERROR, "",
								"Found no projects  with the Python nature in the workspace."));
					} else if (projectsLst.size() == 1) {
						found[0] = projectsLst.get(0);
					} else {
						RunInUiThread.sync(new Runnable() {

							@Override
							public void run() {
								ProjectSelectionDialog dialog = new ProjectSelectionDialog(EditorUtils.getShell(),
										PythonNature.PYTHON_NATURE_ID);
								dialog.setMessage("Choose the project that'll provide the interpreter and\n"
										+ "PYTHONPATH to be used in the launch of the file.");
								if (dialog.open() == Window.OK) {
									Object firstResult = dialog.getFirstResult();
									if (firstResult instanceof IProject) {
										found[0] = firstResult;
									} else {
										found[0] = new CoreException(
												new Status(IStatus.ERROR, "", "Expected project to be selected."));
									}
								}
							}
						});
					}

					if (found[0] == null) {
						return null;
					}
					if (found[0] instanceof IProject) {
						project = (IProject) found[0];
					} else {
						if (found[0] instanceof CoreException) {
							throw (CoreException) found[0];
						} else {
							throw new CoreException(
									new Status(IStatus.ERROR, "", "Expected project, found: " + found[0]));
						}
					}
				}
				IInterpreterManager pythonInterpreterManager = getInterpreterManager(project);
				String projName = project.getName();
				ILaunchConfigurationWorkingCopy createdConfiguration = LaunchConfigurationCreator
						.createDefaultLaunchConfiguration(resource, getLaunchConfigurationType(),
								LaunchConfigurationCreator.getDefaultLocation(resource, false), // it'll be made
																								// relative later on
								pythonInterpreterManager, projName);
					
				// Common Tab Arguments
				CommonTab tab = new CommonTab();
				tab.setDefaults(createdConfiguration);
				tab.dispose();
				return createdConfiguration;
			}
		};
		return launchShortcut;
	}
}
