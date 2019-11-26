package com.onpositive.musket.data.core;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.musket.data.core.filters.FilterRegistry;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.musket.data.table.ITabularDataSet;

public interface IDataSet extends Iterable<IItem>{
	
	Collection<? extends IItem>items();
	
	public static final String ENCODING = "ENCODING";
	
	int length();

	public default <T> T as(Class<T>z){
		return z.cast(this);
	}
	
	IDataSetDelta compare(IDataSet d);

	default boolean isEmpty() {
		return items().isEmpty();
	}
	default int size() {
		return items().size();
	}
	
	@SuppressWarnings("unchecked")
	default Iterator<IItem> iterator() {
		return (Iterator<IItem>) items().iterator();
	}
	
	public default Map<String,IItem>itemMap(){
		LinkedHashMap<String, IItem>items=new LinkedHashMap<String, IItem>();
		this.items().forEach(v->{
			items.put(v.id(), v);
		});
		return items;
	}

	IItem item(int num);

	public default String name() {
		return "";
	}
	
	public default IFilterProto[] filters() {
		ArrayList<IFilterProto> filters = new FilterRegistry().getFilters(this);
		return filters.toArray(new IFilterProto[filters.size()]);
	}
	public default IVisualizerProto[] visualizers() {
		return new IVisualizerProto[0]; 
	}
	
	public default IAnalizerProto[] analizers() {
		ArrayList<IAnalizerProto> filters = new FilterRegistry().getAnalizers(this);
		return filters.toArray(new IAnalizerProto[filters.size()]);
	}
	public void setSettings(IVisualizerProto proto,Map<String,Object>parameters);

	IDataSet subDataSet(String string, List<? extends IItem> arrayList);

	IDataSet withPredictions(IDataSet t2);

	default Map<String,Object> getSettings(){return new LinkedHashMap<>();}
	
	public default IVisualizerProto getVisualizer() {return null;}
	
	public default List<IVisualizerProto> getVisualizers() {
		return Collections.singletonList(getVisualizer());
	}

	default IDataSet withPredictions(File f2) {
		return withPredictions(DataSetIO.load("file://"+f2.getAbsolutePath(),getEncoding()).as(ITabularDataSet.class));
	}
	
	public List<DescriptionEntry>description();

	public List<ConversionAction>conversions();
	
	default String getEncoding() {
		Map<String, Object> settings = getSettings();
		if (settings.containsKey(ENCODING)) {
			return (String) settings.get(ENCODING);
		}
		return "UTF-8";
	}
	
	IDataSet getParent();
	
	IDataSet getRoot();
	
}