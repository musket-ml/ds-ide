package com.onpositive.dside.ui.editors;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.python.pydev.plugin.PyStructureConfigHelpers;
import org.python.pydev.shared_core.callbacks.ICallback;
import org.python.pydev.ui.wizards.project.IWizardNewProjectNameAndLocationPage;

import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.wizards.MusketProjectWizard;
import com.onpositive.musket_core.ExportForWeb;
import com.onpositive.semantic.model.api.property.Predicate;

public class WebServiceProjectWizard extends MusketProjectWizard{

	protected IProject originalProject;
	protected String expriment;
	
	public WebServiceProjectWizard(IProject prj,String experiment) {
		this.originalProject=prj;
		this.expriment=experiment;
	}
	
	protected IWizardNewProjectNameAndLocationPage createProjectPage() {
		return new WebServiceProjectNameAndLoacation("Setting project properties");
	}
	
	@Override
	protected void createAndConfigProject(IProject newProjectHandle, IProjectDescription description,
			String projectType, String projectInterpreter, IProgressMonitor monitor,
			Object... additionalArgsToConfigProject) throws CoreException {
		// TODO Auto-generated method stub
		ICallback<List<IContainer>, IProject> getSourceFolderHandlesCallback = this.getSourceFolderHandlesCallback;
		ICallback<List<IPath>, IProject> getExistingSourceFolderHandlesCallback = this.getExistingSourceFolderHandlesCallback;

		PyStructureConfigHelpers.createPydevProject(description, newProjectHandle, monitor, projectType,
				projectInterpreter, getSourceFolderHandlesCallback, null, getExistingSourceFolderHandlesCallback);
		IFolder folder = newProjectHandle.getFolder("assets");
		folder.create(true, true, monitor);
		newProjectHandle.getFolder("src").getFile("service.py").create(WebServiceProjectWizard.class.getResourceAsStream("/templates/service.py"),true, monitor);
		monitor.setTaskName("Copying experiment resources");
		com.onpositive.musket_core.Utils.copyDirInner(new File(expriment), folder.getLocation().toFile(),new Predicate<File>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean apply(File arg0) {
				if (arg0.getAbsolutePath().replace('\\', '/').equals(expriment)) {
					return true;
				}
				if (arg0.isDirectory()) {
					return arg0.getName().equals("weights");
				}
				return true;
			}
		});
		com.onpositive.musket_core.Utils.copyDirInner(originalProject.getFolder("modules").getLocation().toFile(), folder.getLocation().toFile(),new Predicate<File>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean apply(File arg0) {
				return true;
			}
		});
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				TaskManager.perform(new ExportForWeb(originalProject, expriment,newProjectHandle));		
			}
		});
		
		folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}
	protected String getSourceFolderPath() {
		return "src";
	}
}
