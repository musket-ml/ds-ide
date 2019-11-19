package com.onpositive.dside.ui.editors;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.python.pydev.ui.wizards.project.PythonProjectWizard;

import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.ui.generic.IKnowsImageObject;

@Display("dlf/export.dlf")
public class ExportOptions {

	
	public static abstract class ExportOption implements IKnowsImageObject{
		String name;
		String icon;
		
		public ExportOption(String string, String string2) {
			this.name=string;
			this.icon=string2;
		}

		public abstract void run(IProject prj, String experiment) ;

		@Override
		public String getImageID() {
			return icon;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	public ArrayList<ExportOption>options=new ArrayList<>();
	
	public ExportOptions() {
		options.add(new ExportOption("Export as Rest API","export_web") {
			@Override
			public void run(IProject prj, String experiment) {
				WebServiceProjectWizard newWizard = new WebServiceProjectWizard(prj,experiment);
				
				newWizard.init(PlatformUI.getWorkbench(), new StructuredSelection());
				WizardDialog wizardDialog = new WizardDialog(org.eclipse.swt.widgets.Display.getCurrent().getActiveShell(),newWizard);
				wizardDialog.open();
			}
		});
		options.add(new ExportOption("Export as frozen tensor flow graph","tensorflow") {
			@Override
			public void run(IProject prj, String experiment) {
				ILabelProvider lp= new WorkbenchLabelProvider();
				ITreeContentProvider cp= new WorkbenchContentProvider();
				FolderSelectionDialog fl=new FolderSelectionDialog(org.eclipse.swt.widgets.Display.getCurrent().getActiveShell(), lp, cp);
				fl.setTitle("Please select destination folder");
				fl.setInput(ResourcesPlugin.getWorkspace().getRoot());
				fl.setValidator(new ISelectionStatusValidator() {
					
					@Override
					public IStatus validate(Object[] selection) {
						// TODO Auto-generated method stub
						if (selection.length==0) {
							return new Status(Status.ERROR, "A", "Please select single folder");
						}
						if (selection.length>1) {
							return new Status(Status.ERROR, "A", "Please select single folder");
						}
						if (!(selection[0] instanceof IContainer)) {
							return new Status(Status.ERROR, "A", "Please select single folder");
						}
						return new Status(IStatus.OK, "A","");
					}
				});
				fl.open();
				//System.out.println("Hello world");
			}
		});
	}
	
	@Required("Please select export type")
	public ExportOption selected;
}
