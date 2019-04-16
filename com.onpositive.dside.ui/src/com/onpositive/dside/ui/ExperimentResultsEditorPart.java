package com.onpositive.dside.ui;

import java.util.ArrayList;

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
import org.eclipse.ui.part.EditorPart;

import com.onpositive.musket_core.Experiment;
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

		CTabItem i0 = new CTabItem(f, SWT.NONE);
		i0.setText("Attempts");
		Composite pm = new Composite(f, SWT.NONE);
		Composite c = new Composite(pm, SWT.NONE);

		ArrayList<ExperimentResults> results = experiment.results();
		pm.setLayout(new GridLayout(1, false));
		if (results.size() == 1) {
			Composite ca=new Composite(c, SWT.NONE);
			
			GridLayout layout = new GridLayout(2,false);
			Label ll=new Label(ca, SWT.NONE);
			ll.setFont(JFaceResources.getHeaderFont());
			ll.setText("Primary result: "+experiment.getScore().toString());
			
			ll.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).hint(-1, 20).create());
			Label lla=new Label(ca, SWT.SEPARATOR|SWT.HORIZONTAL);
			lla.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
			layout.verticalSpacing=8;
			layout.horizontalSpacing=12;
			ca.setLayout(layout);
			ArrayList<String> metrics = results.get(0).getMetrics();
			for (String s:metrics) {
				Label ls=new Label(ca, SWT.NONE);
				ls.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
				ls.setText(s+":");
				Object metric = results.get(0).getMetric(s);
				Label lsa=new Label(ca, SWT.NONE);
				lsa.setText(ExperimentsResultViewer.metricString(metric));
			}
			i0.setText("Results");
			pm.setLayout(new FillLayout());
			c.setLayout(new FillLayout());
			i0.setControl(pm);
			c.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		} else {
			ExperimentsResultViewer experimentsResultViewer = new ExperimentsResultViewer(c);
			experimentsResultViewer.setResults(results);
			Control tree = experimentsResultViewer.getControl();
			i0.setControl(pm);
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
