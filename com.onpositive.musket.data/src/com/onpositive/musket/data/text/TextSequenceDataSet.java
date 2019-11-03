package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.table.IColumn;

public class TextSequenceDataSet  implements IDataSet,ITextDataSet{

	public TextSequenceDataSet(ArrayList<Document> docs2) {
		this.docs.addAll(docs2);
		settings.put(GenericDataSet.FONT_SIZE, "13");
		settings.put(GenericDataSet.MAX_CHARS_IN_TEXT, "700");
	}

	public TextSequenceDataSet() {
		settings.put(GenericDataSet.FONT_SIZE, "13");
		settings.put(GenericDataSet.MAX_CHARS_IN_TEXT, "700");
	}

	protected ArrayList<Document>docs=new ArrayList<>();
	private int text;
	
	protected void init() {
		this.text=0;
	}

	public int textPosition() {
		return text;
	}
	
	protected LabelsSet labels;
	
	protected static String TEXT_COLUMN="TEXT_COLUMN";
	
	protected static String CLAZZ_COLUMNS="CLAZZ_COLUMNS";



	protected IColumn textColumn;
	protected ArrayList<IItem> items ;
	protected Map<String, Object> settings = new LinkedHashMap<String, Object>();
	private String name = "";
	

	@Override
	public int length() {
		return items().size();
	}

	@Override
	public List<? extends IItem> items() {
		if (this.items == null) {
			this.items = this.createItems();
		}
		return this.items;
	}

	protected ArrayList<IItem> createItems(){
		ArrayList<IItem> arrayList = new ArrayList<>();
		arrayList.addAll(this.docs);
		return arrayList;
	}

	@Override
	public IDataSetDelta compare(IDataSet d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IItem item(int num) {
		return items().get(num);
	}
	
	@Override
	public Map<String, Object> getSettings() {
		return settings;
	}

	@Override
	public String name() {
		return this.name;
	}
	@Override
	public IVisualizerProto getVisualizer() {
		return new IVisualizerProto() {

			@Override
			public Parameter[] parameters() {
			
				Parameter parameter = new Parameter();
				parameter.name=GenericDataSet.FONT_SIZE;
				Map<String, Object> settings2 = getSettings();
				parameter.defaultValue=settings2.get(GenericDataSet.FONT_SIZE).toString();
				parameter.type=Integer.class;
				
				Parameter parameter1 = new Parameter();
				parameter1.name=GenericDataSet.MAX_CHARS_IN_TEXT;
				parameter1.defaultValue=settings2.get(GenericDataSet.MAX_CHARS_IN_TEXT).toString();
				parameter1.type=Integer.class;
				
				return new Parameter[] {parameter,parameter1};
			}

			@Override
			public String name() {
				return "Card visualizer";
			}

			@Override
			public String id() {
				return "Card visualizer";
			}

			@Override
			public Supplier<Collection<String>> values(IDataSet ds) {
				return null;
			}
		};
	}

	@Override
	public void setSettings(IVisualizerProto proto, Map<String, Object> parameters) {
		this.settings.putAll(parameters);
	}

	@Override
	public IDataSet subDataSet(String string, List<? extends IItem> arrayList) {
		TextSequenceDataSet textSequenceDataSet = new TextSequenceDataSet((ArrayList<Document>) arrayList);
		textSequenceDataSet.name=string;
		return textSequenceDataSet;
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		return null;
	}

	@Override
	public List<DescriptionEntry> description() {
		return new ArrayList<>();
	}

	@Override
	public List<ConversionAction> conversions() {
		return new ArrayList<>();
	}
}