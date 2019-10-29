package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.VisualizationSpec;
import com.onpositive.musket.data.core.VisualizationSpec.ChartType;

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
		LinkedHashMap<Object, ArrayList<IItem>> maps1 = AbstractAnalizer.optimize(maps);
		maps1.keySet().forEach(v -> {
			results.add(ds.subDataSet(v.toString(), maps1.get(v)));

		});

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
				return new VisualizationSpec("", "", ChartType.BAR);
			}
		};
	}

	protected abstract ArrayList<Object> group(IItem v);
}
