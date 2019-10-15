package com.onpositive.musket.data.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IAnalizerProto;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.images.actions.BasicImageDataSetActions.ConversionAction;
import com.onpositive.musket.data.table.ITabularDataSet;

public class GenericDataSet implements IDataSet{

	private DataSetSpec spec;
	
	public DataSetSpec getSpec() {
		return spec;
	}
	
	private ITabularDataSet base;
	protected String name="";

	public GenericDataSet(DataSetSpec spec, ITabularDataSet t1) {
		this.spec=spec;
		this.base=t1;		
	}
	private List<IItem>items;
	
	
	@Override
	public List<? extends IItem> items() {
		if (items==null) {
			items=base.items().stream().map(x->{
				return new GenericItem(this,x);
				
			}).collect(Collectors.toList());;
		}
		return items;
	}
	@Override
	public int length() {
		return items().size();
	}
	@Override
	public IDataSetDelta compare(IDataSet d) {
		return base.compare(d);
	}
	@Override
	public IItem item(int num) {
		return items().get(num);
	}
	@Override
	public String name() {
		return this.name;
	}
	@Override
	public void setSettings(IVisualizerProto proto, Map<String, Object> parameters) {
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public IDataSet subDataSet(String string, List<? extends IItem> arrayList) {
		GenericDataSet genericDataSet = new GenericDataSet(spec, base);
		genericDataSet.name=string;
		genericDataSet.items=(List<IItem>) arrayList;
		return genericDataSet;
	}
	
	@Override
	public IDataSet withPredictions(IDataSet t2) {
		return null;
	}
	
	@Override
	public List<DescriptionEntry> description() {
		ArrayList<DescriptionEntry>entry=new ArrayList<>();
		return entry;
	}
	
	@Override
	public List<ConversionAction> conversions() {
		return new ArrayList<>();
	}

	@Override
	public IAnalizerProto[] analizers() {
		return IDataSet.super.analizers();
	}
}