package com.onpositive.musket.data.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
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
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.generic.GenerateMusketWrapperSettings.ColumnCoding;
import com.onpositive.musket.data.table.ICSVOVerlay;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class GenericDataSet implements IDataSet,ICSVOVerlay,IPythonStringGenerator{

	static final String FONT_SIZE = "Font size";
	static final String VISIBLE_COLUMNS="Visible Columns";
	static final String MAX_CHARS_IN_TEXT="Trim text to";
	
	private DataSetSpec spec;
	
	public DataSetSpec getSpec() {
		return spec;
	}
	
	private ITabularDataSet base;
	protected String name="";

	public GenericDataSet(DataSetSpec spec, ITabularDataSet t1) {
		this.spec=spec;
		this.base=t1;
		settings.put(FONT_SIZE, "12");
		settings.put(MAX_CHARS_IN_TEXT, "300");
		settings.put(VISIBLE_COLUMNS, "");
		if (spec.layout!=null) {
			settings.put("layout", spec.layout.toOptions());
		}
	}
	private List<IItem>items;
	
	protected HashMap<String, Object>settings=new HashMap<>();
	
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
		//settings.clear();
		settings.putAll(parameters);
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
		String inputs="["+ss.inputColumns.stream().map(x->'"'+x.column.getColumn().caption()+'"').collect(Collectors.joining(","))+"]";
		String outputs="["+ss.outputColumns.stream().map(x->'"'+x.column.getColumn().caption()+'"').collect(Collectors.joining(","))+"]";
		results.add(inputs);
		results.add(outputs);
		results.add(spec.representer.getImageDirsString());
		ArrayList<GenerateMusketWrapperSettings.ColumnCoding>all=new ArrayList<>();
		all.addAll(ss.inputColumns);
		all.addAll(ss.outputColumns);
		String ctypes="{"+all.stream().map(x->'"'+x.column.getColumn().caption()+'"'+":"+'"'+x.coder+'"').collect(Collectors.joining(","))+"}";
		results.add(ctypes);
		ArrayList<ColumnCoding> inputColumns = ss.inputColumns;
		LinkedHashMap<String, ArrayList<String>> icolumns = buildColumnGroups(inputColumns);
		if (!icolumns.isEmpty()) {
			results.add("input_groups={"+icolumns.entrySet().stream().map(x->reprEntry(x)).collect(Collectors.joining(","))+"}");
		}
		icolumns = buildColumnGroups(ss.outputColumns);
		if (!icolumns.isEmpty()) {
			results.add("output_groups={"+icolumns.entrySet().stream().map(x->reprEntry(x)).collect(Collectors.joining(","))+"}");
		}
		return results;
	}
	private String reprEntry(Entry<String, ArrayList<String>> x) {
		return '"'+x.getKey()+'"'+":["+x.getValue().stream().collect(Collectors.joining(","))+"]";
	}
	protected LinkedHashMap<String, ArrayList<String>> buildColumnGroups(ArrayList<ColumnCoding> inputColumns) {
		LinkedHashMap<String, ArrayList<String>>icolumns=new LinkedHashMap<>();
		
		inputColumns.forEach(i->{
			if (i.group!=null&&!i.group.trim().isEmpty()) {
				String trim = i.group.trim();
				ArrayList<String> arrayList = icolumns.get(trim);
				if (arrayList==null) {
					arrayList=new ArrayList<>();
					icolumns.put(trim, arrayList);
				}
				arrayList.add('"'+i.column.getColumn().caption()+'"');
			}
		});
		return icolumns;
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
	
	@Override
	public IVisualizerProto getVisualizer() {
		return new IVisualizerProto() {

			@Override
			public Parameter[] parameters() {
			
				Parameter parameter = new Parameter();
				parameter.name=FONT_SIZE;
				parameter.defaultValue=getSettings().get(FONT_SIZE).toString();
				parameter.type=Integer.class;
				
				Parameter parameter1 = new Parameter();
				parameter1.name=MAX_CHARS_IN_TEXT;
				parameter1.defaultValue=getSettings().get(MAX_CHARS_IN_TEXT).toString();
				parameter1.type=Integer.class;
				
				Parameter parameter2 = new Parameter();
				parameter2.name=VISIBLE_COLUMNS;
				parameter2.defaultValue=getSettings().get(VISIBLE_COLUMNS).toString();
				parameter2.type=Columns.class;
				return new Parameter[] {parameter,parameter1,parameter2};
			}

			@Override
			public String name() {
				return "Card visualizer";
			}

			@Override
			public String id() {
				return "Cart visualizer";
			}

			@Override
			public Supplier<Collection<String>> values() {
				return null;
			}
		};
	}
	@Override
	public Map<String, Object> getSettings() {
		return settings;
	}
	
}