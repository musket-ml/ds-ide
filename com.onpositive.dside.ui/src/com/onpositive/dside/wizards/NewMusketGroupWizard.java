package com.onpositive.dside.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
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
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.navigator.CommonNavigator;
import org.python.pydev.core.log.Log;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class NewMusketGroupWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;

	

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	private ExperimentGroupParams experimentParams;

	@Override
	public void addPages() {
		this.addPage(new DLFWizardPage("New Experiment Group") {

			
			
			@Override
			public void createControl(Composite parent) {
				setImageDescriptor(SWTImageManager.getDescriptor("new_exp_wiz"));
				el = new RootElement(parent);
				setTitle("New Experiment Group");
				setMessage("Create new experiment group");
				experimentParams = new ExperimentGroupParams();
				ISelection selection2 = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getSelection();
				if (selection2 instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection2).getFirstElement();
					
					if (firstElement instanceof IAdaptable) {
						IAdaptable mm = (IAdaptable) firstElement;
						IResource adapter = mm.getAdapter(IResource.class);
						if (adapter != null) {
							org.eclipse.core.resources.IProject prj = adapter.getProject();
							experimentParams.project = prj.getName();
						}
					}
				}
				Binding bn = new Binding(experimentParams);

				IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(experimentParams, null,
						null);
				IUIElement<?> createWidget = widgetObject.createWidget(bn);
				el.add((AbstractUIElement<?>) createWidget);
				Control control = (Control) createWidget.getControl();
				//control.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				//parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				setControl(control);
				this.setPageComplete(false);
				bn.addStatusChangeListener(new IStatusChangeListener() {

					@Override
					public void statusChanged(IHasStatus bnd, CodeAndMessage cm) {
						setPageComplete(!cm.isError());
						setErrorMessage(cm.getMessage());
					}
				});
				setErrorMessage(bn.getStatus().getMessage());
				((Composite) control).layout(true, true);
			}
		});
	}

	@Override
	public boolean performFinish() {
		// define the operation to create a new project
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException {
				org.eclipse.core.resources.IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(experimentParams.project);
				IFolder folder = project.getFolder("experiments");
				if (!folder.exists()) {
					folder.create(true, true, monitor);
				}
				String group = experimentParams.group;
				if (group==null) {
					group="";
				}
				Path path = new Path(group);
				IFolder folder2=folder;
				if (!path.isEmpty()) {					
					folder2 = folder.getFolder(path);
					if (!folder2.exists()) {
						folder2.create(true, true, monitor);
					}
				}	
				IFolder folder3 = folder2.getFolder(new Path("1"));
				if (!folder3.exists()) {
					folder3.create(true, true, monitor);
				}
				IFile file = folder3.getFile("config.yaml");
				file.create(NewMusketExperimentWizard.class.getResourceAsStream("/templates/experiment.yaml"), true, monitor);
			}
		};

		// run the operation to create a new project
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
				.getActivePage().getActivePart();
		activePart.getCommonViewer().refresh();
		return true;
	}

}