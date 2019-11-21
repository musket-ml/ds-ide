package com.onpositive.musket.data.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.filters.FilterRegistry;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.musket.data.table.ITabularDataSet;

public abstract class AbstractDataSet<T extends IItem> implements IDataSet, Cloneable {
	
	protected List<T> items;
	
	protected String name = "";

	public <T> T as(Class<T> z) {
		return z.cast(this);
	}

	public boolean isEmpty() {
		return items().isEmpty();
	}

	public int size() {
		return items().size();
	}

	@SuppressWarnings("unchecked")
	public Iterator<IItem> iterator() {
		return (Iterator<IItem>) items().iterator();
	}

	public Map<String, IItem> itemMap() {
		LinkedHashMap<String, IItem> items = new LinkedHashMap<String, IItem>();
		this.items().forEach(v -> {
			items.put(v.id(), v);
		});
		return items;
	}

	public String name() {
		return this.name;
	}

	public IFilterProto[] filters() {
		ArrayList<IFilterProto> filters = new FilterRegistry().getFilters(this);
		return filters.toArray(new IFilterProto[filters.size()]);
	}

	public IVisualizerProto[] visualizers() {
		return new IVisualizerProto[0];
	}

	public IAnalizerProto[] analizers() {
		ArrayList<IAnalizerProto> filters = new FilterRegistry().getAnalizers(this);
		return filters.toArray(new IAnalizerProto[filters.size()]);
	}

	public AbstractDataSet subDataSet(String name, List<? extends IItem> arrayList) {
		List<? extends IItem> cloned = arrayList.stream().map(x -> x.clone()).collect(Collectors.toList());
		try {
			AbstractDataSet rs = (AbstractDataSet) this.clone();
			rs.items = cloned;
			rs.name = name;
			return rs;		
		} catch (CloneNotSupportedException e) {			
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> getSettings() {
		return new LinkedHashMap<>();
	}

	public IVisualizerProto getVisualizer() {
		return null;
	}

	public IDataSet withPredictions(File f2) {
		return withPredictions(DataSetIO.load("file://" + f2.getAbsolutePath(),getEncoding()).as(ITabularDataSet.class));
	}
}