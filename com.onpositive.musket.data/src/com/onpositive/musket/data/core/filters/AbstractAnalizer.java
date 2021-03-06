package com.onpositive.musket.data.core.filters;

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.ChartData;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.VisualizationSpec;
import com.onpositive.musket.data.core.VisualizationSpec.ChartType;

public abstract class AbstractAnalizer {

	public IAnalizeResults analize(IDataSet ds) {
		LinkedHashMap<Object, ArrayList<IItem>> maps = new LinkedHashMap<Object, ArrayList<IItem>>();
		ds.items().parallelStream().forEach(v -> {
			IItem v1=remap(v);
			Object group = group(v1);
			synchronized (AbstractAnalizer.this) {
				ArrayList<IItem> arrayList = maps.get(group);
				if (arrayList == null) {
					arrayList = new ArrayList<IItem>();
					maps.put(group, arrayList);
				}
				arrayList.add(v);	
			}			
		});
		OptimizationResult optimized = optimize(maps);
		LinkedHashMap<Object, ArrayList<IItem>> mapsNew = optimized.getItems();
		return toDs(ds, mapsNew,maps);
	}
	
	protected Function<IItem, IItem>converter;

	public Function<IItem, IItem> getConverter() {
		return converter;
	}

	public void setConverter(Function<IItem, IItem> converter) {
		this.converter = converter;
	}

	private IItem remap(IItem v) {
		if (converter!=null) {
			return converter.apply(v);
		}
		return v;
	}

	protected IAnalizeResults toDs(IDataSet ds, LinkedHashMap<Object, ArrayList<IItem>> mapsNew, LinkedHashMap<Object, ArrayList<IItem>> maps) {
		ArrayList<IDataSet> results = new ArrayList<IDataSet>();

		mapsNew.keySet().forEach(v -> {
			results.add(ds.subDataSet(v.toString(), mapsNew.get(v)));

		});
		VisualizationSpec visualizationSpec = getVisualizationSpec();
		ChartData.BasicChartData basicChartData = new ChartData.BasicChartData();
		maps.keySet().forEach(k->{
			basicChartData.values.put(k.toString(), (double)maps.get(k).size());
		});
		if (visualizationSpec.full==null) {
			visualizationSpec.full=basicChartData;
		}
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
				
				return visualizationSpec;
			}
		};
	}
	
	public static class OptimizationResult {
		
		private LinkedHashMap<Object, ArrayList<IItem>> items;
		
		private LinkedHashMap<Object, ArrayList<Object>> classes;

		public OptimizationResult(LinkedHashMap<Object, ArrayList<IItem>> items,
				LinkedHashMap<Object, ArrayList<Object>> classes) {
			super();
			this.items = items;
			this.classes = classes;
		}

		public LinkedHashMap<Object, ArrayList<IItem>> getItems() {
			return items;
		}

		public LinkedHashMap<Object, ArrayList<Object>> getClasses() {
			return classes;
		}
	}

	static OptimizationResult optimize(LinkedHashMap<Object, ArrayList<IItem>> maps) {
		if (maps.size() > 20) {
			boolean allNumber = true;
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (Object o : maps.keySet()) {
				if (!(o instanceof Number)) {
					
					if (o.equals("None")) {
						continue;
					}
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
				ArrayList<IItem>none=new ArrayList<>();
				for (Object o : maps.keySet()) {
					if (o.equals("None")) {
						none.addAll(maps.get(o));
						continue;
					}
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
				while (removed&&items.size()>6) {
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
				LinkedHashMap<Object, ArrayList<Object>> bclasses = new LinkedHashMap<>();
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
					bclasses.put(s, new ArrayList<Object>(Arrays.asList(new Object[] {s})) );
				}				
				if (!none.isEmpty()) {
					bitems.put("None", none);
					Set<Object> s = Collections.newSetFromMap(new LinkedHashMap<Object, Boolean>());
					s.addAll(maps.keySet());
					s.removeAll(bitems.keySet());
					bclasses.put("None", new ArrayList<Object>(s));
				}
				OptimizationResult result = new OptimizationResult(bitems, bclasses);
				return result;
			}
			else {
				ArrayList<Entry>entr=new ArrayList<>();
				maps.keySet().forEach(k->{
					entr.add(new Entry(k, maps.get(k)));
				});
				Collections.sort(entr);
				LinkedHashMap<Object, ArrayList<IItem>> bitems = new LinkedHashMap<>();
				LinkedHashMap<Object, ArrayList<Object>> bclasses = new LinkedHashMap<>();
				
				for (int i=0;i<20;i++) {
					Entry entry = entr.get(i);
					bitems.put(entry.clazz, entry.items);
					bclasses.put(entry.clazz, new ArrayList<Object>(Arrays.asList(new Object[] {entry.clazz})) );
				}
				ArrayList<IItem>allOthers=new ArrayList<>();
				for (int i=20;i<entr.size();i++) {
					allOthers.addAll(entr.get(i).items);
				}
				String remainigKey = "Remaining "+(entr.size()-20);
				bitems.put(remainigKey, allOthers);
				Set<Object> s = Collections.newSetFromMap(new LinkedHashMap<Object, Boolean>());
				s.addAll(maps.keySet());
				s.removeAll(bitems.keySet());
				bclasses.put(remainigKey, new ArrayList<Object>(s));
				OptimizationResult result = new OptimizationResult(bitems, bclasses);
				return result;
			}
		}
		LinkedHashMap<Object, ArrayList<Object>> classes = new LinkedHashMap<Object, ArrayList<Object>>(maps.entrySet().stream().collect(Collectors.toMap(
	            e -> e.getKey(),
	            e -> new ArrayList<Object>(Arrays.asList(new Object[]{e.getKey()}))
	        )));
		OptimizationResult result = new OptimizationResult(maps, classes);		
		return result;
	}
	
	static class Entry implements Comparable<Entry>{
		Object clazz;
		ArrayList<IItem>items;
		
		public Entry(Object clazz, ArrayList<IItem> items) {
			super();
			this.clazz = clazz;
			this.items = items;
		}
		@Override

		public int compareTo(Entry arg0) {
			return arg0.items.size()-this.items.size();
		}
	}

	protected abstract Object group(IItem v);

	protected VisualizationSpec getVisualizationSpec() {
		return new VisualizationSpec("", "", ChartType.PIE);
	}
}
