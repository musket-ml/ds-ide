package com.onpositive.dside.tasks;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.python.pydev.debug.core.Constants;
import org.python.pydev.debug.ui.launching.FileOrResource;
import org.python.pydev.debug.ui.launching.LaunchShortcut;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.IHasName;
import com.onpositive.yamledit.io.YamlIO;

public class TaskManager {

	private static ArrayList<Runnable> onJobComplete = new ArrayList<>();
	
	private static IdentityHashMap<ILaunch, TaskStatus> tasks = new IdentityHashMap<>();

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

	public static void addJobListener(Runnable r) {
		onJobComplete.add(r);
	}

	public static void removeJobListener(Runnable r) {
		onJobComplete.remove(r);
	}

	public static class TaskStatus {
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
				Object loadAs = YamlIO.loadAs(new StringReader(new String(readAllBytes)), task.resultClass());
				task.afterCompletion(loadAs);
			} catch (Exception e) {
				DSIDEUIPlugin.log(e);
			}
			this.reported = true;
		}

	}

	@SuppressWarnings("deprecation")
	public static void perform(IServerTask<?> task) {
		task.beforeStart();
		String dump = YamlIO.dump(task);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.console.ConsoleView");
		} catch (PartInitException e1) {
			DSIDEUIPlugin.log(e1);
		}
		

		try {
			IProject[] projects = task.getProjects();
			LaunchShortcut launchShortcut = new MusketLaunchShortcut(projects, task.getPreferredLaunchConfigType());
			((MusketLaunchShortcut) launchShortcut).setPreferredLaunchConfigType(task.getPreferredLaunchConfigType());

			
			ILaunchConfigurationWorkingCopy launchConfig = launchShortcut
					.createDefaultLaunchConfigurationWithoutSaving(
							new FileOrResource[] { new FileOrResource(LaunchScriptFileProvider.getScriptFile()) });
			Set<String> modeSet = new HashSet<String>(Arrays.asList("run", "debug"));
			((ILaunchConfigurationWorkingCopy) launchConfig).setPreferredLaunchDelegate(modeSet, 
					task.getPreferredLaunchConfigType());
			if (task instanceof IHasName) {
				launchConfig.rename(((IHasName) task).getName());
			}
			ILaunchConfigurationDelegate preferredDelegate = launchConfig.getType().getDelegate(task.isDebug() ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE);
			
			if (preferredDelegate instanceof IMusketLaunchDelegate) {
				((IMusketLaunchDelegate) preferredDelegate).setTask(task);
			}
			launchConfig.setAttribute(ITaskConstants.YAML_SETTINGS, dump);
			
			if (projects.length > 0) {
				launchConfig.setAttribute(Constants.ATTR_PROJECT,
						projects[0].getName());
			}

			// createDefaultLaunchConfigurationWithoutSaving.setAttribute(Constants.ATTR_WORKING_DIRECTORY,
			// "D:/");
			HashMap<String, String> value = new HashMap<>();
			value.put("PYTHONUNBUFFERED", "1");
			launchConfig.setAttribute(DebugPlugin.ATTR_ENVIRONMENT, value);
			if (task.save()) {
				launchConfig.doSave();
			}
			ILaunch launch = launchConfig.launch(task.isDebug() ? "debug" : "run",
					new NullProgressMonitor());
			

		} catch (Exception e) {
			DSIDEUIPlugin.log(e);
		}

	}

	public static void registerTask(ILaunch launch, TaskStatus status) {
		tasks.put(launch, status);
	}

}
