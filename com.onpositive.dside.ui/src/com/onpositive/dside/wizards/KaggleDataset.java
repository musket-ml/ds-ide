package com.onpositive.dside.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.navigator.CommonNavigator;
import org.python.pydev.core.log.Log;
import org.python.pydev.shared_ui.EditorUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.ui.DatasetTableElement;
import com.onpositive.dside.ui.navigator.ExperimentGroup;
import com.onpositive.musket_core.IProject;
import com.onpositive.musket_core.IServer;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class KaggleDataset extends Wizard implements INewWizard {

	private IStructuredSelection selection;

	public KaggleDataset() {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	private KaggleDatasetParams datasetView;

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		this.addPage(new WizardPage("Hello") {

			@SuppressWarnings("serial")
			@Override
			public void createControl(Composite parent) {
				setImageDescriptor(SWTImageManager.getDescriptor("dataset_wiz"));
				RootElement el = new RootElement(parent);
				setTitle("New Dataset");
				setMessage("Let's have fun");
				datasetView = new KaggleDatasetParams();
				ISelection selection2 = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
				
				if(selection2 instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection2).getFirstElement();
					
					if (firstElement instanceof IAdaptable) {
						IAdaptable mm = (IAdaptable) firstElement;
						IResource adapter = mm.getAdapter(IResource.class);
						if (adapter != null) {
							org.eclipse.core.resources.IProject prj = adapter.getProject();
							datasetView.project = prj.getName();
						}
					}
				}
				
				Binding bn = new Binding(datasetView);

				IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(datasetView, null, null);
				
				IUIElement<?> createWidget = widgetObject.createWidget(bn);
				
				el.add((AbstractUIElement<?>) createWidget);
				
				setControl((Control) createWidget.getControl());
				
				this.setPageComplete(false);
				
				bn.addValidator(new IValidator<Object>() {
					@Override
					public CodeAndMessage isValid(IValidationContext arg0, Object arg1) {
						return datasetView.getItem() == null ? new CodeAndMessage(CodeAndMessage.ERROR, "Selection is empty!") : new CodeAndMessage(CodeAndMessage.OK, "");
					}
					
				});
				
				bn.addStatusChangeListener(new IStatusChangeListener() {
					@Override
					public void statusChanged(IHasStatus bnd, CodeAndMessage cm) {
						setPageComplete(!cm.isError());
						
						setErrorMessage(cm.getMessage());
					}
				});
				setErrorMessage(bn.getStatus().getMessage());
			}
		});
	}
	
	private void download(org.eclipse.core.resources.IProject project) {
		IFolder folder = project.getFolder("data/" + datasetView.getItem().ref);
		
		String fullPath = folder.getLocation().toOSString();
			
		GateWayRelatedTask serverTask = new GateWayRelatedTask(project, new IGateWayServerTaskDelegate() {
			@Override
			public void terminated() {
								
			}
			
			@Override
			public void started(GateWayRelatedTask task) {
				
			}
		});
				
		serverTask.getServer().thenAcceptAsync((IServer server) -> {
			try {
				if(datasetView.isDsDatasetEnabled()) {
					server.downloadDataset(datasetView.getItem().ref, fullPath);
				} else {
					server.downloadCompetitionFiles(datasetView.getItem().ref, fullPath);
				}
				
			} catch(Throwable t) {
				t.printStackTrace();
			}
			
			serverTask.terminate();
		});
		
		TaskManager.perform(serverTask);
	}
	
	private void ensure(IFolder folder, IProgressMonitor monitor) throws CoreException {
		List<IContainer> folders = new ArrayList<IContainer>();
		
		IContainer currentFolder = folder;
		
		while(!currentFolder.exists()) {
			folders.add(0, currentFolder);
			
			currentFolder = (IContainer) currentFolder.getParent();
		}
		
		for(IContainer cnt: folders) {
			if (cnt instanceof IFolder) {
				((IFolder) cnt).create(true, true, monitor);
			}
		}
	}
	
	@Override
	public boolean performFinish() {
		Display currentDisplay = Display.getCurrent();
		
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException {
				org.eclipse.core.resources.IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(datasetView.project);
				
				IFolder folder = project.getFolder("data/" + datasetView.getItem().ref);
				
				ensure(folder, monitor);
				
				currentDisplay.asyncExec(new Runnable() {
					public void run() {
						download(project);						
					}
				});
			}
		};

		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					MessageDialog.openError(getShell(), "Unable to create experiment",
							"Another experiement with the same name already exists.");
				} else {
					ErrorDialog.openError(getShell(), "Unable to create project", null,
							((CoreException) t).getStatus());
				}
			} else {
				// Unexpected runtime exceptions and errors may still occur.
				Log.log(IStatus.ERROR, t.toString(), t);
				MessageDialog.openError(getShell(), "Unable to create experiment", t.getMessage());
			}
			return false;
		}
		CommonNavigator activePart = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView("org.python.pydev.navigator.view");
		if (activePart!=null) {
			activePart.getCommonViewer().refresh();
		}
		return true;
	}

}
