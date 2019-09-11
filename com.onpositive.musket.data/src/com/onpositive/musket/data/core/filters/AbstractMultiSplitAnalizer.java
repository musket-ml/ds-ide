package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;

public abstract class AbstractMultiSplitAnalizer {

	public IAnalizeResults analize(IDataSet ds) {
		LinkedHashMap<Object, ArrayList<IItem>> maps = new LinkedHashMap<Object, ArrayList<IItem>>();
		ds.items().forEach(v -> {
			ArrayList<Object> group = group(v);
			for (Object o : group) {
				ArrayList<IItem> arrayList = maps.get(o.toString());
				if (arrayList == null) {
					arrayList = new ArrayList<IItem>();
					maps.put(o.toString(), arrayList);
				}
				arrayList.add(v);
			}
		});
		ArrayList<IDataSet> results = new ArrayList<IDataSet>();

		maps.keySet().forEach(v -> {
			results.add(ds.subDataSet(v.toString(), maps.get(v)));

		});
		return new IAnalizeResults() {

			@Override
			public int size() {
				return maps.keySet().size();
			}

			@Override
			public String[] names() {
				return maps.keySet().toArray(new String[maps.keySet().size()]);
			}

			@Override
			public IDataSet get(int num) {
				return results.get(num);
			}

			@Override
			public IDataSet getOriginal() {
				return ds;
			}
		};
	}

	protected abstract ArrayList<Object> group(IItem v);
}