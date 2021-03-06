package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.ChartData;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.VisualizationSpec;
import com.onpositive.musket.data.core.VisualizationSpec.ChartType;
import com.onpositive.musket.data.core.filters.AbstractAnalizer.OptimizationResult;
import com.onpositive.musket.data.images.MultiClassSegmentationDataSet;

public abstract class AbstractMultiSplitAnalizer {

	public IAnalizeResults analize(IDataSet ds) {
		LinkedHashMap<Object, ArrayList<IItem>> maps = new LinkedHashMap<Object, ArrayList<IItem>>();
		ds.items().parallelStream().forEach(v -> {
			ArrayList<Object> group = group(v);
			synchronized (AbstractMultiSplitAnalizer.this) {
				for (Object o : group) {
					if (o != null) {
						ArrayList<IItem> arrayList = maps.get(o.toString());
						if (arrayList == null) {
							arrayList = new ArrayList<IItem>();
							maps.put(o.toString(), arrayList);
						}
						arrayList.add(v);
					}
				}
			}
		});
		ArrayList<IDataSet> results = new ArrayList<IDataSet>();
		OptimizationResult optimized = AbstractAnalizer.optimize(maps);
		LinkedHashMap<Object, ArrayList<IItem>> maps1 = optimized.getItems();
		LinkedHashMap<Object, ArrayList<Object>> classes1 = optimized.getClasses();
		maps1.keySet().forEach(v -> {
			IDataSet subDS = ds.subDataSet(v.toString(), maps1.get(v));
			if (subDS instanceof MultiClassSegmentationDataSet) {
				MultiClassSegmentationDataSet subDS2 = (MultiClassSegmentationDataSet)subDS;
				ArrayList<Object> clazzez = classes1.get(v);
				subDS2.setClasses(clazzez);
			}
			results.add(subDS);

		});
		VisualizationSpec visualizationSpec = new VisualizationSpec("", "", ChartType.BAR);
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
				return maps1.keySet().size();
			}

			@Override
			public String[] names() {
				return maps1.keySet().toArray(new String[maps1.keySet().size()]);
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

	protected abstract ArrayList<Object> group(IItem v);
}
