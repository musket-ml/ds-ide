package com.onpositive.musket.data.text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.onpositive.musket.data.actions.BasicDataSetActions;
import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IPythonStringGenerator;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.MultiClassClassificationItem;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.table.ClassColumnsOptimizer;
import com.onpositive.musket.data.table.ComputableColumn;
import com.onpositive.musket.data.table.ICSVOVerlay;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IHasLabels;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class TextClassificationDataSet extends AbstractTextDataSet
		implements IBinaryClassificationDataSet, IMulticlassClassificationDataSet,ICSVOVerlay,IPythonStringGenerator,IHasLabels {

	protected ArrayList<IColumn> clazzColumns;

	protected IColumn clazzColumn;

	protected IColumn binaryColumn;

	protected ArrayList<String> binaryValues = new ArrayList<>();

	protected ArrayList<String> classes = new ArrayList<>();

	private boolean isMulti;
	
	
	
	public TextClassificationDataSet(ITabularDataSet tb, Map<String, Object> options) {
		super(tb.clone(),options);
		Object object = settings.get(CLAZZ_COLUMNS);
		String[] clazz=object.toString().split(",");
		ArrayList<IColumn>cls=new ArrayList<>();
		for (String s:clazz) {
			cls.add(tb.getColumn(s));
		}
		this.clazzColumns=cls;
		this.clazzColumn=cls.get(0);
		init(clazzColumns);
	}
	
	public TextClassificationDataSet(ITabularDataSet base, IColumn textColumn, ArrayList<IColumn> clazzColumns) {
		super(base.clone(), textColumn, null);
		
		for (IColumn c:new ArrayList<>(base.columns())) {
			if (c instanceof ComputableColumn) {
				this.tabularBase.columns().remove(c);
			}
		}
		settings.put(CLAZZ_COLUMNS, clazzColumns.stream().map(x->x.id()).collect(Collectors.joining(",")));
		init(clazzColumns);
	}

	protected void init(ArrayList<IColumn> clazzColumns) {
		
		
		
		this.clazzColumns=clazzColumns;
		
		//this.clazzColumns = clazzColumns;
		if (this.clazzColumns.size() == 1) {
			clazzColumn = this.clazzColumns.get(0);
			binaryColumn = clazzColumn;
			LinkedHashSet<Object> linkedHashSet = new LinkedHashSet<>(clazzColumn.values());
			binaryValues = new ArrayList<>(
					linkedHashSet.stream().map(x -> ("" + x).trim()).collect(Collectors.toList()));
			Collections.sort(binaryValues);
		} else {

			IColumn clazzColumn = ClassColumnsOptimizer.createCompositeColumn(clazzColumns);
			this.clazzColumn=clazzColumn;
			this.tabularBase = this.tabularBase.addColumn(clazzColumn);

			for (IColumn m : this.clazzColumns) {
				LinkedHashSet<Object> linkedHashSet = new LinkedHashSet<>(m.values());
				if (linkedHashSet.size() == 2) {
					binaryColumn = m;
					binaryValues = new ArrayList<>(
							linkedHashSet.stream().map(x -> ("" + x).trim()).collect(Collectors.toList()));
					Collections.sort(binaryValues);
				}
			}
			if (binaryColumn==null) {
				binaryColumn=this.clazzColumn;
			}
		}
		LinkedHashSet<String> allValues = new LinkedHashSet<>();
		isMulti = false;
		for (Object x : clazzColumn.uniqueValues()) {
			ArrayList<String> splitByClass = MultiClassClassificationItem.splitByClass(x.toString().trim(),labels);
			if (splitByClass.size()>1) {
				isMulti=true;
			}
			allValues.addAll(splitByClass);
		}
		classes = new ArrayList<String>(allValues);
		Collections.sort(classes);
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public List items() {
		return super.items();
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		TextClassificationDataSet ts=new TextClassificationDataSet(t2.as(ITabularDataSet.class),this.textColumn, this.clazzColumns);
		TextClassificationDataSetWithPredictions textClassificationDataSetWithPredictions = new TextClassificationDataSetWithPredictions(tabularBase, textColumn, clazzColumns, ts);
		textClassificationDataSetWithPredictions.setLabels(this.labels);
		return textClassificationDataSetWithPredictions;
	}

	@Override
	protected ArrayList<IItem> createItems() {
		ArrayList<IItem> items = new ArrayList<>();
		tabularBase.items().forEach(v -> {
			items.add(new TextItem(this, v));
		});
		return items;
	}

	@Override
	public boolean isExclusive() {
		return !this.isMulti;
	}

	@Override
	public List<String> classNames() {
		
		return classes;
	}

	@Override
	public IBinaryClassificationDataSet forClass(String clazz) {
		ITabularDataSet filter = filter(clazz, tabularBase, clazzColumn.caption());
		TextClassificationDataSet textClassificationDataSet = new TextClassificationDataSet(filter, this.textColumn, this.clazzColumns);
		textClassificationDataSet.labels=this.labels;
		return textClassificationDataSet;
	}

	protected static ITabularDataSet filter(String clazz, ITabularDataSet base2, String clazzColumn) {
		ITabularDataSet filter = base2.filter(clazzColumn, x -> {
			return MultiClassClassificationItem.splitByClass(x.toString(),null).contains(clazz);
		});
		return filter;
	}

	@Override
	public boolean isPositive(TextItem textItem) {
		Object value = binaryColumn.getValue(textItem.tabularBase);
		String vl = value.toString().trim();
		if (binaryValues.indexOf(vl) > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Object binaryLabel(TextItem textItem) {
		String value = binaryColumn.getValueAsString(textItem.tabularBase).trim();
		if (binaryValues.size() == 2) {
			String vl = value.toString().trim();
			return vl;
		}
		if (binaryValues.indexOf(value) == 0) {
			return value;
		} else {
			return "Other";
		}
	}
	@Override
	public List<ConversionAction> conversions() {
		return BasicDataSetActions.getActions(this);
	}

	@Override
	public ITabularDataSet original() {
		return tabularBase;
	}
 
	@Override
	public String getImportString() {
		return "from musket_text import text_datasets"+System.lineSeparator()+"from musket_core import datasets";
	}
	
	@Override
	public List<ITabularItem> represents(IItem i) {
		TextItem it=(TextItem) i;
		return Collections.singletonList(it.tabularBase);
	}
	@Override
	public String generatePythonString(String sourcePath,Object model) {
		return "text_datasets."+getPythonName()+"("+this.getDataSetArgs(sourcePath).stream().collect(Collectors.joining(","))+")";
	}

	protected String getPythonName() {
		if (this.classNames().size()==2) {
			if (this.isExclusive()) {
				return "BinaryTextClassificationDataSet";
			}
			else {
				return "MultiClassTextClassificationDataSet";
			}
		}
		if (this.isExclusive()) {
			return "CategoryTextClassificationDataSet";
		}
		else {
			return "MultiClassTextClassificationDataSet";
		}
	}

	protected  ArrayList<String> getDataSetArgs(String sourcePath) {
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add('"'+sourcePath+'"');
		arrayList.add('"'+textColumn.id()+'"');
		arrayList.add('"'+clazzColumn.caption()+'"');		
		return arrayList;
	}

	@Override
	public Object modelObject() {
		return null;
	}

	public TextClassificationDataSet withIds(ITabularDataSet filter) {
		ITabularDataSet withIds = this.tabularBase.withIds(filter);
		return new TextClassificationDataSet(withIds, this.settings);
	}

	@Override
	public void setLabels(LabelsSet labelsSet) {
		this.labels=labelsSet;
	}

	@Override
	public LabelsSet labels() {
		return this.labels;
	}
	
}