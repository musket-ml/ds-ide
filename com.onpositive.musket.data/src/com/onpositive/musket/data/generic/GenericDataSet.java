package com.onpositive.musket.data.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.onpositive.musket.data.actions.BasicDataSetActions;
import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IAnalizerProto;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IPythonStringGenerator;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.table.ICSVOVerlay;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class GenericDataSet implements IDataSet,ICSVOVerlay,IPythonStringGenerator{

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
		entry.add(new DescriptionEntry("Kind", "Generic dataset"));
		entry.add(new DescriptionEntry("Columns", this.spec.layout.infos().size()));
		entry.add(new DescriptionEntry("Size", this.size()));
		return entry;
	}
	
	@Override
	public List<ConversionAction> conversions() {
		return BasicDataSetActions.getActions(this);
	}

	@Override
	public IAnalizerProto[] analizers() {
		return IDataSet.super.analizers();
	}
	@Override
	public ITabularDataSet original() {
		return base;
	}
	@Override
	public List<ITabularItem> represents(IItem i) {
		GenericItem ti=(GenericItem) i;
		return Collections.singletonList(ti.base);
	}
	@Override
	public Object modelObject() {
		GenerateMusketWrapperSettings generateMusketWrapperSettings = new GenerateMusketWrapperSettings();
		generateMusketWrapperSettings.allColumns.addAll(spec.layout.infos());
		return generateMusketWrapperSettings;
	}
	@Override
	public String generatePythonString(String sourcePath, Object modelObject) {
		return "genericcsv.GenericCSVDataSet("+this.getDataSetArgs(sourcePath,modelObject).stream().collect(Collectors.joining(","))+")";
		
	}
	private Collection<String> getDataSetArgs(String sourcePath,Object modelObject) {
		GenerateMusketWrapperSettings ss=(GenerateMusketWrapperSettings) modelObject;
		ArrayList<String>results=new ArrayList<>();
		results.add("\""+sourcePath+"\"");
		String inputs="["+ss.inputColumns.stream().map(x->'"'+x.getColumn().caption()+'"').collect(Collectors.joining(","))+"]";
		String outputs="["+ss.outputColumns.stream().map(x->'"'+x.getColumn().caption()+'"').collect(Collectors.joining(","))+"]";
		results.add(inputs);
		results.add(outputs);
		results.add(spec.representer.getImageDirsString());
		ArrayList<ColumnInfo>all=new ArrayList<>();
		all.addAll(ss.inputColumns);
		all.addAll(ss.outputColumns);
		String ctypes="{"+all.stream().map(x->'"'+x.getColumn().caption()+'"'+":"+'"'+getTypeName(x)+'"').collect(Collectors.joining(","))+"}";
		results.add(ctypes);
		return results;
	}
	String getTypeName(ColumnInfo info) {
		try {
			IColumnType newInstance = info.preferredType().newInstance();
			return newInstance.typeId(info.getColumn());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public String getImportString() {
		return "from musket_core import datasets,genericcsv";
	}
	
}