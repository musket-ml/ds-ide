package com.onpositive.dside.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.MultiEditorInput;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.dside.ui.datasets.CompareCSVDataSets;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.Experiment.PredictionPair;
import com.onpositive.musket_core.ExperimentResults;

public class ExperimentResultsEditorPart extends EditorPart {

	private ExperimentComposite comp;
	private Experiment experiment;

	public ExperimentResultsEditorPart(Experiment e) {
		this.experiment = e;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
//		formToolkit = new FormToolkit(Display.getCurrent());
//		Form createForm = formToolkit.createForm(parent);
//		createForm.setText("Experiment");
//		createForm.getBody().setLayout(new FillLayout());
		CTabFolder f = new CTabFolder(parent, SWT.NONE);
		comp = new ExperimentComposite(f);

//		CTabItem i1=new CTabItem(f, SWT.NONE);
//		i1.setText("Configuration");
//		i1.setControl(comp);
		List<PredictionPair> predictions = experiment.getPredictions();
		CTabItem i0 = new CTabItem(f, SWT.NONE);
		i0.setText("Attempts");
		Composite pm = new Composite(f, SWT.NONE);
		Composite c = new Composite(pm, SWT.NONE);

		ArrayList<ExperimentResults> results = experiment.results();
		pm.setLayout(new GridLayout(1, false));
		if (results.size() == 1) {
			Composite ca = new Composite(c, SWT.NONE);

			GridLayout layout = new GridLayout(2, false);
			Label ll = new Label(ca, SWT.NONE);
			ll.setFont(JFaceResources.getHeaderFont());
			ll.setText("Primary result: " + experiment.getScore().toString());

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
				Label h=new Label(ca, SWT.None);
				h.setText("Predictions:");
				h.setFont(JFaceResources.getHeaderFont());
				h.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(0, 20).create());
				h=new Label(ca, SWT.SEPARATOR|SWT.HORIZONTAL);
				h.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(0, 0).create());
				for (PredictionPair p : predictions) {
					ImageHyperlink hl = new ImageHyperlink(ca, SWT.NONE);
					hl.setText(p.name);
					hl.setUnderlined(true);
					hl.setImage(SWTImageManager.getImage("stage"));
					hl.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
					hl.addHyperlinkListener(new IHyperlinkListener() {

						@Override
						public void linkExited(HyperlinkEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void linkEntered(HyperlinkEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void linkActivated(HyperlinkEvent e) {
							IFile iFile = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(p.groundTruth.toURI())[0];
							IFile iFile1 = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(p.prediction.toURI())[0];
							CompareCSVDataSets.open(iFile, iFile1);
						}
					});
					;
				}
			}
			i0.setText("Results");
			pm.setLayout(new FillLayout());
			c.setLayout(new FillLayout());
			i0.setControl(pm);
			c.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		} else {
			ExperimentResults summary = experiment.getSummary();
			ArrayList<String> metrics = summary.getMetrics();
			Composite ca = new Composite(c, SWT.NONE);

			GridLayout layout = new GridLayout(4, false);
			Label ll = new Label(ca, SWT.NONE);
			ll.setFont(JFaceResources.getHeaderFont());
			ll.setText("Best Parameters: ");

			ll.setLayoutData(GridDataFactory.fillDefaults().span(4, 1).hint(-1, 20).create());
			for (String s : metrics) {
				if (s.equals("min")||s.equals("max")||s.equals("mean")) {
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
			Composite c1=new Composite(c, SWT.NONE);
			ExperimentsResultViewer experimentsResultViewer = new ExperimentsResultViewer(c1);
			experimentsResultViewer.setResults(results);
			Control tree = experimentsResultViewer.getControl();
			i0.setControl(pm);
			c.setLayout(new GridLayout(1, false));
			ca.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
			c1.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
			c.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		}

		CTabItem i = new CTabItem(f, SWT.NONE);
		i.setText("Logs");
		i.setControl(comp);
		comp.setExperiment(experiment);
		for (ExperimentResults ea : results) {
			ea.getMetrics();
		}

		f.setSelection(0);
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
