package com.onpositive.musket.data.core.filters;

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.VisualizationSpec;
import com.onpositive.musket.data.core.VisualizationSpec.ChartType;

public abstract class AbstractAnalizer {

	public IAnalizeResults analize(IDataSet ds) {
		LinkedHashMap<Object, ArrayList<IItem>> maps = new LinkedHashMap<Object, ArrayList<IItem>>();
		ds.items().parallelStream().forEach(v -> {
			Object group = group(v);
			synchronized (AbstractAnalizer.this) {
				ArrayList<IItem> arrayList = maps.get(group);
				if (arrayList == null) {
					arrayList = new ArrayList<IItem>();
					maps.put(group, arrayList);
				}
				arrayList.add(v);	
			}			
		});
		LinkedHashMap<Object, ArrayList<IItem>> mapsNew = optimize(maps);
		return toDs(ds, mapsNew);
	}

	protected IAnalizeResults toDs(IDataSet ds, LinkedHashMap<Object, ArrayList<IItem>> mapsNew) {
		ArrayList<IDataSet> results = new ArrayList<IDataSet>();

		mapsNew.keySet().forEach(v -> {
			results.add(ds.subDataSet(v.toString(), mapsNew.get(v)));

		});
		return new IAnalizeResults() {

			@Override
			public int size() {
				return mapsNew.keySet().size();
			}

			@Override
			public String[] names() {
				return mapsNew.keySet().toArray(new String[mapsNew.keySet().size()]);
			}

			@Override
			public IDataSet get(int num) {
				return results.get(num);
			}

			@Override
			public IDataSet getOriginal() {
				return ds;
			}

			@Override
			public IDataSet getFiltered() {
				return ds;
			}

			@Override
			public VisualizationSpec visualizationSpec() {
				return getVisualizationSpec();
			}
		};
	}

	private LinkedHashMap<Object, ArrayList<IItem>> optimize(LinkedHashMap<Object, ArrayList<IItem>> maps) {
		if (maps.size() > 20) {
			boolean allNumber = true;
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (Object o : maps.keySet()) {
				if (!(o instanceof Number)) {
					allNumber = false;
					break;
				} else {
					Number nm = (Number) o;
					if (nm.doubleValue() < min) {
						min = nm.doubleValue();
					}
					if (nm.doubleValue() > max) {
						max = nm.doubleValue();
					}
				}
			}
			if (allNumber) {
				double st = (max - min) / 20;
				LinkedHashMap<Point2D, ArrayList<IItem>> items = new LinkedHashMap<>();
				int totalSize = 0;
				for (Object o : maps.keySet()) {
					Number nm = (Number) o;
					int group = (int) ((nm.doubleValue() - min) / st);
					double minV = min + group * (max - min) / 20;
					double maxV = min + (group + 1) * (max - min) / 20;
					if (st > 20) {
						minV = (int) minV;
						maxV = (int) maxV;
					}

					Point2D.Double double1 = new Point2D.Double(minV, maxV);
					ArrayList<IItem> arrayList = items.get(double1);
					if (arrayList == null) {
						arrayList = new ArrayList<>();
						items.put(double1, arrayList);
					}
					ArrayList<IItem> arrayList2 = maps.get(o);
					totalSize = totalSize + arrayList2.size();
					arrayList.addAll(arrayList2);
				}
				boolean removed = true;
				while (removed) {
					removed=false;
					ArrayList<Point2D> arrayList = new ArrayList<>(items.keySet());
					arrayList.sort(new Comparator<Point2D>() {

						@Override
						public int compare(Point2D o1, Point2D o2) {
							if (o1.getX() < o2.getX()) {
								return 1;
							}
							if (o1.getX() > o2.getX()) {
								return -1;
							}
							return 0;
						}
					});
					int a = 0;
					for (Point2D p : arrayList) {
						if (items.get(p).size() < totalSize / 100) {
							if (a==arrayList.size()-1) {
								break;
							}
							Point2D point2d = arrayList.get(a + 1);
							removed=true;
							ArrayList<IItem> arrayList2 = items.get(point2d);
							arrayList2.addAll(items.get(p));
							items.remove(p);
							items.remove(point2d);
							items.put(new Point2D.Double(point2d.getX(), p.getY()), arrayList2);
							break;
						}
						a = a + 1;
					}
				}
				LinkedHashMap<Object, ArrayList<IItem>> bitems = new LinkedHashMap<>();
				ArrayList<Point2D> arrayList = new ArrayList<>(items.keySet());
				arrayList.sort(new Comparator<Point2D>() {

					@Override
					public int compare(Point2D o1, Point2D o2) {
						if (o1.getX() < o2.getX()) {
							return -1;
						}
						if (o1.getX() > o2.getX()) {
							return 1;
						}
						return 0;
					}
				});
				for (Point2D p: arrayList) {
					String s=NumberFormat.getInstance().format(p.getX())+"-"+NumberFormat.getInstance().format(p.getY());
					bitems.put(s,items.get(p));
				}				
				return bitems;
			}
		}
		return maps;
	}

	protected abstract Object group(IItem v);

	protected VisualizationSpec getVisualizationSpec() {
		return new VisualizationSpec("", "", ChartType.PIE);
	}
}
