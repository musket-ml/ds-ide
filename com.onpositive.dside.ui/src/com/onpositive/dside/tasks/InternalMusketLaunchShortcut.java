package com.onpositive.dside.tasks;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.jface.window.Window;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.debug.ui.launching.FileOrResource;
import org.python.pydev.debug.ui.launching.LaunchConfigurationCreator;
import org.python.pydev.debug.ui.launching.LaunchShortcut;
import org.python.pydev.plugin.nature.PythonNature;
import org.python.pydev.shared_core.callbacks.ICallback;
import org.python.pydev.shared_core.utils.ArrayUtils;
import org.python.pydev.shared_ui.EditorUtils;
import org.python.pydev.shared_ui.dialogs.ProjectSelectionDialog;
import org.python.pydev.shared_ui.utils.RunInUiThread;

public class InternalMusketLaunchShortcut extends LaunchShortcut {

	private static final String DEFAULT_LAUNCH_TYPE = "com.onpositive.dside.musket.launch";
	private IProject[] projects;
	private String preferredLaunchConfigType;

	public InternalMusketLaunchShortcut() {
		this(new IProject[0], DEFAULT_LAUNCH_TYPE);
	}

	public InternalMusketLaunchShortcut(IProject[] projects, String preferredLaunchConfigType) {
		this.projects = projects;
		this.preferredLaunchConfigType = preferredLaunchConfigType;
	}
	
	@Override
	protected String getLaunchConfigurationType() {
		if (preferredLaunchConfigType != null) {
			return preferredLaunchConfigType;
		}
		return DEFAULT_LAUNCH_TYPE;
	}

	public ILaunchConfigurationWorkingCopy createDefaultLaunchConfigurationWithoutSaving(FileOrResource[] resource)
			throws CoreException {
		IProject project = null;
		if (projects.length > 0) {
			project = projects[0];
		} 
		if (project == null) {
			project = tryGetProject(resource);
		}
		
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

	private IProject tryGetProject(FileOrResource[] resources) {
		for (FileOrResource fileOrResource : resources) {
			if (fileOrResource.resource != null) {
				return fileOrResource.resource.getProject();
			}
		}
		return null;
	}

	public void setPreferredLaunchConfigType(String preferredLaunchDelegate) {
		this.preferredLaunchConfigType = preferredLaunchDelegate;
	}
	
	

}
