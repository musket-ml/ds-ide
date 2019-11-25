package com.onpositive.dside.tasks.analize;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.python.pydev.shared_ui.EditorUtils;

import com.onpositive.datasets.visualisation.ui.views.BasicQuestionAnswerer;
import com.onpositive.dside.dto.ExportDataSet;
import com.onpositive.dside.dto.GetPossibleAnalisisInfo;
import com.onpositive.dside.dto.GetPossibleAnalisisResult;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.ui.ModelEvaluationSpec;
import com.onpositive.dside.ui.WorkbenchUIUtils;
import com.onpositive.dside.ui.datasets.CompareCSVDataSets;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.project.DataProjectAccess;
import com.onpositive.musket_core.Experiment;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/analizePredictions.dlf")
public class AnalizeDataSet implements IGateWayServerTaskDelegate {

	static ModelEvaluationSpec lastSpec;
	static String lastDataSet;
	
	protected Experiment experiment;
	protected ModelEvaluationSpec model = new ModelEvaluationSpec(true, true, true);
	protected boolean debug;

	@RealmProvider(expression = "experiment.DataSets")
	@Required
	protected String dataset = "validation";
	private AnalistsView showView;
	private GateWayRelatedTask task;
	public boolean data=false;
	
	@Caption("Export to CSV")
	protected boolean exportToCSV;
	
	@Caption("Export Ground Truth to CSV")
	protected boolean exportGroundTruthToCSV;

	public AnalizeDataSet(Experiment experiment) {
		super();
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
	public void terminated() {
		org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (showView!=null) {
					IWorkbenchPage activePage = WorkbenchUIUtils.getActivePage();
					if (activePage != null) {
						activePage.hideView(showView);
					}
				}
			}
		});
	}

	@Override
	public void started(GateWayRelatedTask task) {
		this.task=task;
		lastDataSet=dataset;
		lastSpec=this.model;
		if (this.exportToCSV||this.exportGroundTruthToCSV) {
			task.perform(new ExportDataSet(model,dataset,this.experiment.getPath().toOSString(),this.exportGroundTruthToCSV), String.class, (r)->{
				String[] split = r.split("::::");
				if (split.length==2) {
					IFile iFile = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new File(split[1]).toURI())[0];
					IFile iFile1 = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new File(split[0]).toURI())[0];
					CompareCSVDataSets.open(iFile, iFile1);
				}
				else {
					File file = new File(split[0]);
					IFile iFile1 = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(file.toURI())[0];
					try {
					IDataSet dataSet2 = DataProjectAccess.getDataSet(file,new BasicQuestionAnswerer());
					if (dataSet2!=null) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(iFile1), "com.onpositive.datasets.visualisation.ui.datasetEditor");
					}
					}catch (Exception e) {
						EditorUtils.openFile(file);
						// TODO: handle exception
					}
				}
				task.terminate();
			},(e)->{
				onError(e);
				task.terminate();
			});
			return;
		}
		task.perform(new GetPossibleAnalisisInfo(model,dataset,this.experiment.getPath().toOSString()), GetPossibleAnalisisResult.class, (r)->{
			display(r);
		},(e)->{
			onError(e);
		});
		
	}

	private void onError(Throwable e) {
		org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageDialog.openError(org.eclipse.swt.widgets.Display.getCurrent().getActiveShell(), "Error", e.getMessage());
			}
		});
	}

	private void display(GetPossibleAnalisisResult r) {
//		int size = r.size();
//		
//		ArrayList<IDataSet>ds=new ArrayList<>();
//		for (int i=0;i<size;i++) {
//			IDataSet iDataSet = r.get(i);
//			ds.add(iDataSet);
//			System.out.println(iDataSet.name()+":"+iDataSet.len());
//		}
		try {
			showView = (AnalistsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.onpositive.dside.tasks.analize");			
			showView.setResults(r,model,dataset,this.experiment,task,data);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isDebug() {
		return this.debug;
	}

}
