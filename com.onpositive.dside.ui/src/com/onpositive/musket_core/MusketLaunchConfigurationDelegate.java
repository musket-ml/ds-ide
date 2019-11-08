package com.onpositive.musket_core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.core.log.Log;
import org.python.pydev.debug.core.Constants;
import org.python.pydev.debug.core.PydevDebugPlugin;
import org.python.pydev.debug.ui.launching.AbstractLaunchConfigurationDelegate;
import org.python.pydev.debug.ui.launching.InvalidRunException;
import org.python.pydev.debug.ui.launching.PythonRunnerConfig;
import org.python.pydev.shared_core.SharedCorePlugin;

import com.onpositive.dside.tasks.IMusketLaunchDelegate;
import com.onpositive.dside.tasks.IServerTask;
import com.onpositive.dside.tasks.ITaskConstants;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.tasks.TaskManager.TaskStatus;

public class MusketLaunchConfigurationDelegate extends AbstractLaunchConfigurationDelegate implements IMusketLaunchDelegate{
	
	
	private static final String TEMP_FILE_PREFFIX = "ds_ide";
	private IServerTask<?> task;
	
	/**
	 * @return
	 */
	@Override
	protected String getRunnerConfigRun(ILaunchConfiguration conf, String mode, ILaunch launch) {
		return PythonRunnerConfig.RUN_REGULAR;
	}

	@Override
	public void launch(ILaunchConfiguration conf, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
		try {
			String yaml = conf.getAttribute(ITaskConstants.YAML_SETTINGS, (String)null);
			
			Path taskFilePath = Files.createTempFile(TEMP_FILE_PREFFIX, "task").toAbsolutePath();
			Path resultFilePath = Files.createTempFile(TEMP_FILE_PREFFIX, "result").toAbsolutePath();
			
			HashSet<String> modes = new HashSet<>();
			modes.add("run");
			modes.add("debug");
			Files.write(taskFilePath, yaml.getBytes());
			
			ArrayList<String> args = new ArrayList<>();
			args.add(taskFilePath.toString());
			args.add(resultFilePath.toString());
			if (!(conf instanceof ILaunchConfigurationWorkingCopy)) {
				conf = conf.copy(conf.getName());
			}
			((ILaunchConfigurationWorkingCopy) conf).setAttribute(Constants.ATTR_PROGRAM_ARGUMENTS,
					args.stream().collect(Collectors.joining(" ")));
			
			IServerTask<?> task = obtainTask(conf);
		
		if (monitor == null)

		{
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask("Preparing configuration", 3);

			PythonRunnerConfig runConfig = new PythonRunnerConfig(conf, mode, getRunnerConfigRun(conf, mode, launch));

			monitor.worked(1);
			try {
				PythonRunner.run(runConfig, launch, monitor);
			} catch (IOException e) {
				Log.log(e);
				finishLaunchWithError(launch);
				throw new CoreException(
						PydevDebugPlugin.makeStatus(IStatus.ERROR, "Unexpected IO Exception in Pydev debugger", null));
			}
			TaskStatus status = new TaskStatus((IServerTask<Object>) task, resultFilePath);
			if (launch.isTerminated()) {
				status.report(launch);
			}
			TaskManager.registerTask(launch, status);
			task.afterStart(launch);
		} catch (IOException e) {
			handleError(launch, e);
		} catch (final InvalidRunException e) {
			handleError(launch, e);
		} catch (final MisconfigurationException e) {
			handleError(launch, e);
		}
	}
	
	protected IServerTask<?> obtainTask(ILaunchConfiguration conf) {
		if (task != null) {
			return task;
		}
		return null;
	}

	private void finishLaunchWithError(ILaunch launch) {
        try {
            launch.terminate();

            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            launchManager.removeLaunch(launch);
        } catch (Throwable x) {
            Log.log(x);
        }
    }
	private void handleError(ILaunch launch, final Exception e) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Invalid launch configuration",
                        "Unable to make launch because launch configuration is not valid",
                        SharedCorePlugin.makeStatus(IStatus.ERROR, e.getMessage(), e));
            }
        });
        finishLaunchWithError(launch);
    }

	@Override
	public void setTask(IServerTask<?> task) {
		this.task = task;
	}
	
}
