package com.onpositive.musket_core;

import java.io.StringReader;
import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobFunction;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.IProgressConstants;
import org.yaml.snakeyaml.Yaml;

import com.onpositive.dside.ui.Activator;
import com.onpositive.semantic.model.api.labels.LabelAccess;

public class ServerManager {

	private static IOConsoleOutputStream newOutputStream;
	private static IOConsoleOutputStream errorOutputStream;
	private static boolean inited;

	static void initConsole() {
		IOConsole console = new IOConsole("Server", null);
		console.activate();
		newOutputStream = console.newOutputStream();
		errorOutputStream = console.newOutputStream();
		errorOutputStream.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IOConsole[] { console });
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
	}

	static ArrayList<Runnable> onJobComplete = new ArrayList<>();

	public static void addJobListener(Runnable r) {
		onJobComplete.add(r);
	}

	public static void removeJobListener(Runnable r) {
		onJobComplete.remove(r);
	}

	public static void perform(Object task) {
		if (!inited) {
			initConsole();
		}
		String dump = new Yaml().dump(task);
		String label = LabelAccess.getLabel(task);
		Job job = Job.create("Performing: " + label, (IJobFunction) monitor -> {
			// do something long running
			// ...
			Object taskResult = null;
			try {
				IServer server = Activator.getDefault().getServer();
				ProgressMonitorWrapper mw = new ProgressMonitorWrapper(monitor, newOutputStream, errorOutputStream);
				String result = server.performTask(dump, mw);

				if (result != null) {
					taskResult = new Yaml().loadAs(new StringReader(result), Object.class);
				}
				System.out.println(result);
			} finally {
				onJobComplete.forEach(r -> r.run());
				if (task instanceof IHasAfterCompletionTasks) {
					((IHasAfterCompletionTasks) task).afterCompletion(taskResult);
				}
			}
			if (monitor.isCanceled()){
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		});
		org.eclipse.jface.action.Action value = new org.eclipse.jface.action.Action() {
			public void run() {
				System.out.println("Hello");
			}
		};
		value.setText("View results");
		job.setProperty(IProgressConstants.ACTION_PROPERTY, value);
		job.setProperty(IProgressConstants.KEEP_PROPERTY, true);
		job.setUser(true);
		// Start the Job
		job.schedule();
	}
}
