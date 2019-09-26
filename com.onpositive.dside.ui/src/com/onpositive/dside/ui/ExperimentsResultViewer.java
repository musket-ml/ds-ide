package com.onpositive.dside.ui;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.musket_core.ExperimentResults;
import com.onpositive.musket_core.Result;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public class ExperimentsResultViewer extends TreeViewer {

	public ExperimentsResultViewer(Composite parent) {
		super(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}

	public void setResults(Collection<ExperimentResults> rs) {

		LinkedHashSet<String> metrics = new LinkedHashSet<>();
		rs.forEach(v -> {
			if (!v.isAll()) {
				metrics.addAll(v.getMetrics());
			}			
		});
		for (TreeColumn c : this.getTree().getColumns()) {
			c.dispose();
		}
		ArrayList<String> ms = new ArrayList<>(metrics);
		
		Collections.sort(ms);
		ms.add(0, "Attempt");
		ArrayList<String> msa = new ArrayList<>();
		TreeColumnLayout ll = new TreeColumnLayout(true);
		for (String s : ms) {

			TreeColumn m = new TreeColumn(this.getTree(), s.equals("Attempt") ? SWT.LEFT : SWT.RIGHT);
			m.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (getTree().getSortColumn() != m) {
						getTree().setSortColumn(m);

					} else {
						if (getTree().getSortDirection() == SWT.UP) {
							getTree().setSortDirection(SWT.DOWN);
						} else
							getTree().setSortDirection(SWT.UP);
					}
					this.resort(s, getTree().getSortDirection());
				}

				private void resort(String s, int sortDirection) {
					setComparator(new ViewerComparator() {
						@Override
						public int compare(Viewer viewer, Object e1, Object e2) {
							Result r1 = (Result) e1;
							Result r2 = (Result) e2;
							Object metric1 = r1.getMetric(s);
							Object metric2 = r2.getMetric(s);
							if (metric1 != null && metric2 != null) {
								if (metric1 instanceof java.util.Map) {
									metric1 = ((java.util.Map) metric1).get("mean");
								}
								if (metric2 instanceof java.util.Map) {
									metric2 = ((java.util.Map) metric2).get("mean");
								}
							}
							Comparable<Object> mm = (Comparable) metric1;
							if (mm == null || metric1 == null) {
								return 0;
							}
							return sortDirection == SWT.UP ? mm.compareTo(metric2) : -mm.compareTo(metric2);
						}
					});

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			ll.setColumnData(m, new ColumnWeightData(2, true));
			msa.add(s);
			m.setText(s.replace('_', ' '));
		}
		this.getTree().setHeaderVisible(true);
		this.setContentProvider(new ITreeContentProvider() {

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

			@Override
			public Object getParent(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return rs.stream().filter(x -> !x.isAll()).toArray();
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return new Object[0];
			}
		});
		this.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(ILabelProviderListener listener) {

			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				Result r = (Result) element;
				if (r == null) {
					return "";
				}
				Object metric = r.getMetric(ms.get(columnIndex));
				return metricString(metric);
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex==0) {
					return SWTImageManager.getImage("stage");
				}
				return null;
			}
		});
		((Composite) this.getControl().getParent()).setLayout(ll);
		this.setInput(rs);
	}

	public static String metricString(Object metric) {
		if (metric == null) {
			return "";
		}
		if (metric instanceof java.util.Map) {
			Double max = (Double) ((java.util.Map) metric).get("max");
			Double min = (Double) ((java.util.Map) metric).get("min");
			Double mean = (Double) ((java.util.Map) metric).get("mean");
			NumberFormat instance = NumberFormat.getInstance();
			instance.setMaximumFractionDigits(4);
			return "[" + instance.format(min) + ", " + instance.format(mean) + ", " + instance.format(max)
					+ "]";
		}
		if (metric instanceof Double) {
			NumberFormat instance = NumberFormat.getInstance();
			instance.setMaximumFractionDigits(4);
			return instance.format(metric);
		}
		return (String) metric.toString();
	}
}
