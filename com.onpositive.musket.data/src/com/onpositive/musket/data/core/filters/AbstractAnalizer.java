package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;

public abstract class AbstractAnalizer {

	public IAnalizeResults analize(IDataSet ds) {
		LinkedHashMap<Object, ArrayList<IItem>>maps=new LinkedHashMap<Object, ArrayList<IItem>>();
		ds.items().forEach(v->{
			Object group=group(v);
			ArrayList<IItem> arrayList = maps.get(group.toString());
			if (arrayList==null) {
				arrayList=new ArrayList<IItem>();
				maps.put(group.toString(), arrayList);
			}
			arrayList.add(v);
		});
		ArrayList<IDataSet>results=new ArrayList<IDataSet>();
		
		maps.keySet().forEach(v->{
			results.add(ds.subDataSet(v.toString(),maps.get(v)));
			
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

			@Override
			public IDataSet getFiltered() {
				return ds;
			}
		};
	}

	protected abstract Object group(IItem v) ;
}
