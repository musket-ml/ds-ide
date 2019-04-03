package com.onpositive.dside.tasks.analize;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.dside.dto.GetPossibleAnalisisInfo;
import com.onpositive.dside.dto.GetPossibleAnalisisResult;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.ui.ModelEvaluationSpec;
import com.onpositive.musket_core.Experiment;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/analizePredictions.dlf")
public class AnalizeDataSet implements IGateWayServerTaskDelegate {

	protected Experiment experiment;
	protected ModelEvaluationSpec model = new ModelEvaluationSpec(true, true, true);

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
	
	static ModelEvaluationSpec lastSpec;
	static String lastDataSet; 

	protected boolean debug;

	@RealmProvider(expression = "experiment.DataSets")
	@Required
	protected String dataset = "validation";
	private AnalistsView showView;
	private GateWayRelatedTask task;
	public boolean data=false;

	@Override
	public void terminated() {
		org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (showView!=null) {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(showView);
				}
			}
		});
	}

	@Override
	public void started(GateWayRelatedTask task) {
		this.task=task;
		lastDataSet=dataset;
		lastSpec=this.model;
		task.perform(new GetPossibleAnalisisInfo(model,dataset,this.experiment.getPath().toOSString()), GetPossibleAnalisisResult.class, (r)->{
			display(r);
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
