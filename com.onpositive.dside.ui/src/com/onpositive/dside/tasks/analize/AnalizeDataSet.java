package com.onpositive.dside.tasks.analize;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.python.pydev.shared_ui.EditorUtils;

import com.onpositive.datasets.visualisation.ui.views.BasicQuestionAnswerer;
import com.onpositive.dside.dto.GetPossibleAnalisisInfo;
import com.onpositive.dside.dto.GetPossibleAnalisisResult;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.WorkbenchUIUtils;
import com.onpositive.dside.ui.datasets.CompareCSVDataSets;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.project.DataProjectAccess;
import com.onpositive.musket_core.Experiment;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/analizePredictions.dlf")
public class AnalizeDataSet extends ExportDataSetTaskDelegate {

	private GateWayRelatedTask task;
	
	private AnalistsView showView;

	public AnalizeDataSet(Experiment experiment) {
		super(experiment);		
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
			task.perform(createExportTask(), String.class, (result)->{
				String[] split = result.split("::::");
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
						DSIDEUIPlugin.log(e);
					}
				}
				finishTask(task);
			},(error)->{
				onError(error);
				finishTask(task);
			});
			return;
		}
		task.perform(new GetPossibleAnalisisInfo(model,dataset,this.experiment.getPath().toOSString()), GetPossibleAnalisisResult.class, (result)->{
			display(result);
		},(error)->{
			onError(error);
		});
		
	}

	private void display(GetPossibleAnalisisResult result) {
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
			showView.setResults(result,model,dataset,this.experiment,task,data);
		} catch (PartInitException e) {
			DSIDEUIPlugin.log(e);
		}
	}

}
