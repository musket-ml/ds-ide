package com.onpositive.dside.tasks.analize;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

import com.onpositive.dside.dto.ExportDataSet;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.ModelEvaluationSpec;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ProjectManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/analizePredictions.dlf")
public class ExportDataSetTaskDelegate implements IGateWayServerTaskDelegate {
	
	static ModelEvaluationSpec lastSpec;
	static String lastDataSet;
	
	protected Experiment experiment;
	protected ModelEvaluationSpec model = new ModelEvaluationSpec(true, true, true);
	protected boolean debug;

	@RealmProvider(expression = "experiment.DataSets")
	@Required
	protected String dataset = "validation";
	public boolean data=false;
	
	@Caption("Export to CSV")
	protected boolean exportToCSV;
	
	@Caption("Export Ground Truth to CSV")
	protected boolean exportGroundTruthToCSV;
	
	public ExportDataSetTaskDelegate(Experiment experiment) {
		this.experiment = experiment;
		this.model = experiment.createModelSpec();
		if (lastDataSet!=null) {
			if (experiment.getDataSets().contains(lastDataSet)) {
				this.dataset=lastDataSet;
			}
		}
		if (lastSpec!=null) {
			if (lastSpec.isHasFolds()==this.model.isHasFolds()) {
				if (lastSpec.isHasSeeds()==this.model.isHasSeeds()) {
					if (lastSpec.isHasStages()==this.model.isHasStages()) {
						this.model=lastSpec;
					}	
				}
			}
		}
	}

	@Override
	public void started(GateWayRelatedTask task) {
		lastDataSet=dataset;
		lastSpec=this.model;
		if (this.exportToCSV||this.exportGroundTruthToCSV) {
			task.perform(createExportTask(), String.class, r -> {
				finishTask(task);
			}, e -> {
				onError(e);
				finishTask(task);
			});
		}
	}

	protected void finishTask(GateWayRelatedTask task) {
		task.terminate();
		new Job("Refresh after task finish") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IProject eclipseProject = ProjectManager.getInstance().getEclipseProject(experiment);
				if (eclipseProject != null) {
					try {
						eclipseProject.refreshLocal(IProject.DEPTH_INFINITE, monitor);
					} catch (CoreException e) {
						DSIDEUIPlugin.log(e);
					}
				}
				return Status.OK_STATUS;
			}
			
		}.schedule();
	}

	protected ExportDataSet createExportTask() {
		return new ExportDataSet(model,dataset,this.experiment.getPath().toOSString(),this.exportGroundTruthToCSV);
	}

	@Override
	public void terminated() {
		// Do nothing; Override if needed
	}
	
	public boolean isDebug() {
		return this.debug;
	}
	
	protected void onError(Throwable e) {
		org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageDialog.openError(org.eclipse.swt.widgets.Display.getCurrent().getActiveShell(), "Error", e.getMessage());
			}
		});
	}

}
