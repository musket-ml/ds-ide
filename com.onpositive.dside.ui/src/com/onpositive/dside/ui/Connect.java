package com.onpositive.dside.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.musket_core.DataSet;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentFinder;
import com.onpositive.musket_core.IDataSet;
import com.onpositive.musket_core.Utils;

public class Connect implements IObjectActionDelegate{

	public Connect() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
//		IStructuredSelection sel=(IStructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
//		IAdaptable s=(IAdaptable) sel.getFirstElement();
//		IProject adapter = s.getAdapter(IProject.class);
//		if (adapter!=null) {
//			IPath location = adapter.getLocation();
//			String portableString = location.toPortableString();
//			com.onpositive.musket_core.IProject project = Activator.getDefault().getServer().project(portableString);
//			List<DataSet> datasets = Utils.asList(project.datasets(),x->new DataSet(x));
//			DataSetsView exp;
//			try {
//				exp = (DataSetsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.onpositive.dside.ui.datasets");
//				exp.setDataSets(datasets);
//			} catch (PartInitException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	
//			
//			try {
//				ExperimentsView exp2=(ExperimentsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.onpositive.dside.ui.experiments");
//				exp2.setLocation(Collections.singletonList(adapter));
//			} catch (PartInitException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

}
