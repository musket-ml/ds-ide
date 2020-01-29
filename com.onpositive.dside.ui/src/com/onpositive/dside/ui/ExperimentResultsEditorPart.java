package com.onpositive.dside.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.dside.ui.datasets.CompareCSVDataSets;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.Experiment.PredictionPair;
import com.onpositive.musket_core.ExperimentIO;
import com.onpositive.musket_core.ExperimentLogs;
import com.onpositive.musket_core.ExperimentResults;
import com.onpositive.musket_core.Score;

public class ExperimentResultsEditorPart extends EditorPart {

	private ExperimentComposite comp;
	private Experiment experiment;
	private Composite parent;

	public ExperimentResultsEditorPart(Experiment e) {
		this.experiment = e;
	}

	protected void refreshUI(List<PredictionPair> predictions, List<ExperimentResults> results, List<ExperimentLogs> logs,  Score score,
			ExperimentResults summary) {
		Control[] children = parent.getChildren();
		for (Control control : children) {
			control.dispose();
		}
		CTabFolder folder = new CTabFolder(parent, SWT.NONE);
		comp = new ExperimentComposite(folder);

//		CTabItem i1=new CTabItem(f, SWT.NONE);
//		i1.setText("Configuration");
//		i1.setControl(comp);
		CTabItem tabItem = new CTabItem(folder, SWT.NONE);
		tabItem.setText("Attempts");
		Composite pm = new Composite(folder, SWT.NONE);
		pm.setLayout(new GridLayout(1, false));
		Composite c = new Composite(pm, SWT.NONE);
		c.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		if (results.size() == 1) {
			Composite ca = new Composite(c, SWT.NONE);

			GridLayout layout = new GridLayout(2, false);
			Label ll = new Label(ca, SWT.NONE);
			ll.setFont(JFaceResources.getHeaderFont());
			ll.setText("Primary result: " + score.toString());

			ll.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).hint(-1, 20).create());
			Label lla = new Label(ca, SWT.SEPARATOR | SWT.HORIZONTAL);
			lla.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
			layout.verticalSpacing = 8;
			layout.horizontalSpacing = 12;
			ca.setLayout(layout);
			ArrayList<String> metrics = results.get(0).getMetrics();
			for (String s : metrics) {
				Label ls = new Label(ca, SWT.NONE);
				ls.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
				ls.setText(s + ":");
				Object metric = results.get(0).getMetric(s);
				Label lsa = new Label(ca, SWT.NONE);
				lsa.setText(ExperimentsResultViewer.metricString(metric));
			}
			if (!predictions.isEmpty()) {
				Label h = new Label(ca, SWT.None);
				h.setText("Predictions:");
				h.setFont(JFaceResources.getHeaderFont());
				h.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(0, 20).create());
				h = new Label(ca, SWT.SEPARATOR | SWT.HORIZONTAL);
				h.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(0, 0).create());
				for (PredictionPair predictionPair : predictions) {
					ImageHyperlink hlink = new ImageHyperlink(ca, SWT.NONE);
					hlink.setText(predictionPair.name);
					hlink.setUnderlined(true);
					hlink.setImage(SWTImageManager.getImage("stage"));
					hlink.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
					hlink.addHyperlinkListener(new HyperlinkAdapter() {

						@Override
						public void linkActivated(HyperlinkEvent e) {
							IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
									.findFilesForLocationURI(predictionPair.groundTruth.toURI())[0];
							if (predictionPair.prediction != null) {
								IFile iFile1 = ResourcesPlugin.getWorkspace().getRoot()
										.findFilesForLocationURI(predictionPair.prediction.toURI())[0];
								try {
									iFile.refreshLocal(0, new NullProgressMonitor());
									iFile1.refreshLocal(0, new NullProgressMonitor());
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								CompareCSVDataSets.open(iFile, iFile1);
							}
							else {
								try {
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(iFile),"com.onpositive.datasets.visualisation.ui.datasetEditor");
								} catch (PartInitException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					});
					
				}
			}
			tabItem.setText("Results");
			pm.setLayout(new FillLayout());
			c.setLayout(new FillLayout());
		} else if (summary != null){
			ArrayList<String> metrics = summary.getMetrics();
			Composite ca = new Composite(c, SWT.NONE);

			GridLayout layout = new GridLayout(4, false);
			Label ll = new Label(ca, SWT.NONE);
			ll.setFont(JFaceResources.getHeaderFont());
			ll.setText("Best Parameters: ");

			ll.setLayoutData(GridDataFactory.fillDefaults().span(4, 1).hint(-1, 20).create());
			for (String s : metrics) {
				if (s.equals("min") || s.equals("max") || s.equals("mean")) {
					continue;
				}
				Label ls = new Label(ca, SWT.NONE);
				ls.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
				ls.setText(s + ":");
				Object metric = results.get(0).getMetric(s);
				Label lsa = new Label(ca, SWT.NONE);
				lsa.setText(ExperimentsResultViewer.metricString(metric));
			}
			ca.setLayout(layout);
			Composite c1 = new Composite(c, SWT.NONE);
			ExperimentsResultViewer experimentsResultViewer = new ExperimentsResultViewer(c1);
			experimentsResultViewer.setResults(results);
			
			c.setLayout(new GridLayout(1, false));
			ca.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
			c1.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		} else {
			c.setLayout(new FillLayout());
			Label lbl = new Label(c, SWT.NONE);
			lbl.setText("No summary.yaml file - no summary available");
		}
		tabItem.setControl(pm);

		CTabItem item = new CTabItem(folder, SWT.NONE);
		item.setText("Logs");
		item.setControl(comp);
		comp.setExperiment(experiment, logs);
		for (ExperimentResults curRes : results) {
			curRes.getMetrics();
		}

		folder.setSelection(0);
		parent.layout(true, true);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		Label loadingLbl = new Label(parent,SWT.NONE);
		loadingLbl.setText("Loading results for " + experiment.getPathString());
		//		formToolkit = new FormToolkit(Display.getCurrent());
//		Form createForm = formToolkit.createForm(parent);
//		createForm.setText("Experiment");
//		createForm.getBody().setLayout(new FillLayout());
		Job rerieveResultsJob = new Job("Retrieving experiment results: " + experiment.getProjectPath()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				experiment.invalidateConfig();
				List<PredictionPair> predictions = experiment.getPredictions();
				ArrayList<ExperimentResults> results = ExperimentIO.results(experiment.getPathString());
				ArrayList<ExperimentLogs> logs = ExperimentIO.logs(experiment.getPathString());
				Score score = experiment.getScore();
				ExperimentResults summary = experiment.getSummary();
				Display.getDefault().asyncExec(() -> {
					refreshUI(predictions, results, logs, score, summary);
				});
				return Status.OK_STATUS;
				
			}
			
		};
		rerieveResultsJob.schedule();
		
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setInput(input);
		super.setSite(site);
		setPartName("Results and Logs");
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
