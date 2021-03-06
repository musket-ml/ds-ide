package com.onpositive.dside.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.jface.window.Window;
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

import com.onpositive.dside.ui.IMusketConstants;
import com.onpositive.dside.ui.LaunchConfiguration;
import com.onpositive.musket_core.Experiment;
import com.onpositive.yamledit.io.YamlIO;

public class MusketLaunchShortcut extends LaunchShortcut {

	private static final String DEFAULT_LAUNCH_TYPE = "com.onpositive.dside.musket.launch";

	public MusketLaunchShortcut() {
	}

	
	
	@Override
	protected String getLaunchConfigurationType() {
		return DEFAULT_LAUNCH_TYPE;
	}

	public ILaunchConfigurationWorkingCopy createDefaultLaunchConfigurationWithoutSaving(FileOrResource[] resource)
			throws CoreException {
		IProject project = tryGetProject(resource);
		
		List<Experiment> experiments = getExperiments(resource);
		if (experiments.isEmpty()) {
			throw new CoreException(new Status(IStatus.ERROR, "", "Unable to get experiment configuration to launch for. No valid " + IMusketConstants.MUSKET_CONFIG_FILE_NAME  + " found"));
		}
		LaunchConfiguration configuration = new LaunchConfiguration(experiments);
		
		if (project == null) {
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
				found[0] = new CoreException(
						new Status(IStatus.ERROR, "", "Found no projects  with the Python nature in the workspace."));
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
					throw new CoreException(new Status(IStatus.ERROR, "", "Expected project, found: " + found[0]));
				}
			}
		}
		IInterpreterManager pythonInterpreterManager = getInterpreterManager(project);
		String projName = project.getName();
		FileOrResource[] launchedScripts = new FileOrResource[] { new FileOrResource(LaunchScriptFileProvider.getScriptFile()) };
		ILaunchConfigurationWorkingCopy createdConfiguration = LaunchConfigurationCreator
				.createDefaultLaunchConfiguration(launchedScripts, getLaunchConfigurationType(),
						LaunchConfigurationCreator.getDefaultLocation(launchedScripts, false), // it'll be made
																						// relative later on
						pythonInterpreterManager, projName);

		Set<String> modeSet = new HashSet<String>(Arrays.asList("run", "debug"));
		((ILaunchConfigurationWorkingCopy) createdConfiguration).setPreferredLaunchDelegate(modeSet, 
				configuration.getPreferredLaunchConfigType());
		createdConfiguration.rename(configuration.getName());
		ILaunchConfigurationDelegate preferredDelegate = createdConfiguration.getType().getDelegate(configuration.isDebug() ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE);
		
		if (preferredDelegate instanceof IMusketLaunchDelegate) {
			((IMusketLaunchDelegate) preferredDelegate).setTask(configuration);
		}
		createdConfiguration.setAttribute(ITaskConstants.YAML_SETTINGS, YamlIO.dump(configuration));

		createdConfiguration.setAttribute(Constants.ATTR_PROJECT,project.getName());

		HashMap<String, String> value = new HashMap<>();
		value.put("PYTHONUNBUFFERED", "1");
		createdConfiguration.setAttribute(DebugPlugin.ATTR_ENVIRONMENT, value);
		if (configuration.save()) {
			createdConfiguration.doSave();
		}
		
		// Common Tab Arguments
		CommonTab tab = new CommonTab();
		tab.setDefaults(createdConfiguration);
		tab.dispose();
		return createdConfiguration;
	}

	private List<Experiment> getExperiments(FileOrResource[] resource) {
		List<Experiment> resList = new ArrayList<Experiment>();
		for (FileOrResource fileOrResource : resource) {
			if (IMusketConstants.MUSKET_CONFIG_FILE_NAME.equalsIgnoreCase(getName(fileOrResource))) {
				resList.add(new Experiment(getPath(fileOrResource)));
			}
		}
		return resList;
	}
	
	private String getPath(FileOrResource fileOrResource) {
		return fileOrResource.resource != null ? fileOrResource.resource.getParent().getLocation().toPortableString() : fileOrResource.file.getParentFile().getAbsolutePath();
	}


	private String getName(FileOrResource fileOrResource) {
		return fileOrResource.resource != null ? fileOrResource.resource.getName() : fileOrResource.file.getName();
	}

	private IProject tryGetProject(FileOrResource[] resources) {
		for (FileOrResource fileOrResource : resources) {
			if (fileOrResource.resource != null) {
				return fileOrResource.resource.getProject();
			}
		}
		return null;
	}

}
