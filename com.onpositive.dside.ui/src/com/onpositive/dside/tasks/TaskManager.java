package com.onpositive.dside.tasks;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchesListener;
import org.python.pydev.debug.core.Constants;
import org.python.pydev.debug.ui.launching.FileOrResource;
import org.python.pydev.debug.ui.launching.LaunchShortcut;
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
			this.reported=true;
		}
		
	}
	static HashMap<ILaunch, TaskStatus>tasks=new HashMap<>();
	
	
	
	static {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new ILaunchesListener() {
			
			@Override
			public synchronized void launchesRemoved(ILaunch[] launches) {
				for (ILaunch l:launches) {
					perform(l);
				}
			}
			
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
			public synchronized void launchesChanged(ILaunch[] launches) {
				for (ILaunch l:launches) {
					perform(l);
				}
				
			}
			
			@Override
			public synchronized void launchesAdded(ILaunch[] launches) {
				for (ILaunch l:launches) {
					perform(l);
				}
			}
		});
	}

	public static void perform(IServerTask<Object> task) {
		task.beforeStart();
		LaunchShortcut launchShortcut = new LaunchShortcut() {

			@Override
			protected String getLaunchConfigurationType() {
				return "org.python.pydev.debug.musketLaunchConfigurationType";
			}
		};
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
				codeToRun.add("import sys\n" + 
						"from musket_core import tools,projects\n" + 
						"import yaml\n" + 
						"import threading\n" + 
						"from musket_core import utils\n" + 
						"config = sys.argv[1]\n" + 
						"out = sys.argv[2]\n" + 
						"def main():\n" + 
						"        global config\n" + 
						"        with open(config,\"r\") as f:\n" + 
						"            config=f.read()\n" + 
						"        config = config[1:].replace(\"!!com.onpositive\", \"!com.onpositive\")\n" + 
						"        obj = yaml.load(config)\n" + 
						"        print(obj)\n" + 
						"        results = obj.perform(projects.Workspace(), tools.ProgressMonitor())\n" + 
						"        with open(out,\"w\") as f:\n" + 
						"            f.write(yaml.dump(results))\n" + 
						"main()");
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
			createDefaultLaunchConfigurationWithoutSaving.setPreferredLaunchDelegate(modes,
					"org.python.pydev.debug.musketLaunchConfigurationType");
			ArrayList<String> args = new ArrayList<>();
			args.add(absolutePath.toString());
			args.add(absolutePath2.toString());
			createDefaultLaunchConfigurationWithoutSaving.setAttribute(Constants.ATTR_PROGRAM_ARGUMENTS,args.stream().collect(Collectors.joining(" ")));
			IProject[] project = task.getProject();
			if (project.length>0) {
					createDefaultLaunchConfigurationWithoutSaving.setAttribute(Constants.ATTR_PROJECT, project[0].getName());
			}
			//createDefaultLaunchConfigurationWithoutSaving.setAttribute(Constants.ATTR_WORKING_DIRECTORY, "D:/");
			HashMap<String, String> value = new HashMap<>();
			value.put("PYTHONUNBUFFERED", "1");
			createDefaultLaunchConfigurationWithoutSaving.setAttribute(DebugPlugin.ATTR_ENVIRONMENT, value);
			ILaunch launch = createDefaultLaunchConfigurationWithoutSaving.launch(task.isDebug()?"debug":"run", new NullProgressMonitor());
			TaskStatus value2 = new TaskStatus(task, absolutePath2);
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
}
