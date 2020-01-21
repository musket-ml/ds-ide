package com.onpositive.dside.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class NewKaggleDatasetAction extends MusketProjectAction{


	public NewKaggleDatasetAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		openWizard("com.onpositive.dside.ui.kaggle_dataset");
	}

	public void openWizard(String id) {
		// First see if this is a "new wizard".
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
		// If not check if it is an "import wizard".
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
		}
		// Or maybe an export wizard
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
		}
		try {
			// Then if we have a wizard, open it.
			if (descriptor != null) {
				IWizard wizard = descriptor.createWizard();
				if (wizard instanceof IWorkbenchWizard) {
					IWorkbenchWizard workbenchWizard = (IWorkbenchWizard) wizard;
					workbenchWizard.init(PlatformUI.getWorkbench(), (IStructuredSelection) selection);
				}
				WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());
				wd.open();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}