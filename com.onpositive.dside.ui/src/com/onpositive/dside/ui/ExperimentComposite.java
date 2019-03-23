package com.onpositive.dside.ui;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentLogs;

public class ExperimentComposite extends Composite {

	private ComboViewer selector;
	private ComboViewer metric;
	private Label bs;
	private Image lastImage;
	private ExperimentLogs currentResults;
	private String metric_name;

	public ExperimentComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(4, false));
		Label label = new Label(this,SWT.NONE);
		label.setText("Log:");
		selector = new ComboViewer(this, SWT.READ_ONLY);
		selector.setContentProvider(new ArrayContentProvider());
		selector.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection sl = (StructuredSelection) event.getSelection();
				currentResults = (ExperimentLogs) sl.getFirstElement();
				setResults(currentResults);

			}
		});
		selector.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		Label label1 = new Label(this,SWT.NONE);
		label1.setText("Metric:");
		metric = new ComboViewer(this, SWT.READ_ONLY);
		metric.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection sl = (StructuredSelection) event.getSelection();
				String metric = (String) sl.getFirstElement();
				ExperimentComposite.this.metric_name = metric;
				render();
			}
		});
		metric.setContentProvider(new ArrayContentProvider());
		metric.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		bs = new Label(this, SWT.BORDER);
		bs.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());
		bs.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				// TODO Auto-generated method stub
				render();
			}

			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void render() {
		if (lastImage != null) {
			lastImage.dispose();
		}
		if (this.currentResults == null) {
			return;
		}
		Point size = bs.getSize();
		if (size.x <= 10 || size.y <= 10) {
			return;
		}
		ImageData image = this.currentResults.toImage(metric_name, size.x - 5, size.y - 5);
		lastImage = new Image(Display.getCurrent(), image);
		bs.setBackgroundImage(lastImage);
	}

	@Override
	public void dispose() {
		if (lastImage != null) {
			lastImage.dispose();
		}
		super.dispose();
	}

	public void setResults(ExperimentLogs s) {
		this.currentResults = s;
		ArrayList<String> metrics = s.metrics();
		if (!metrics.contains(this.metric_name)) {
			this.metric.setInput(metrics.toArray());

			this.metric_name = metrics.toArray()[0].toString();
			this.metric.setSelection(new StructuredSelection(this.metric_name));
		}
		render();
	}

	public void setExperiment(Experiment e) {
		
		ArrayList<ExperimentLogs> results = e.logs();
		if (results.isEmpty()) {
			return;
		}
		selector.setInput(results.toArray());
		selector.setSelection(new StructuredSelection(results.get(0)));
		currentResults = (ExperimentLogs) results.get(0);
		render();
	}
}
