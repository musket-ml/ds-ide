package com.onpositive.musket.data.table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;

public class BasicDataSetImpl implements ITabularDataSet,Cloneable{
	
	protected ArrayList<? extends ITabularItem>items=new ArrayList<BasicItem>();
	protected List<? extends IColumn>columns=new ArrayList<IColumn>();
	
	

	public BasicDataSetImpl(ArrayList<? extends ITabularItem> items, List<? extends IColumn> cs) {
		super();
		this.items = items;
		this.columns = cs;
		items.forEach(v->{
			if (v instanceof BasicItem) {
				BasicItem b=(BasicItem) v;
				if (b.source==null) {
					b.source=this;
				}
			}
		});
		this.columns.forEach(v->{
			if (v instanceof Column) {
				((Column) v).owner=this;
			}
		});
	}
	
	public BasicDataSetImpl(ArrayList<ITabularItem> arrayList, List<? extends IColumn> columns2, String idColumn2) {
		this(arrayList,columns2);
		this.setIdColumn(idColumn2);
	}

	protected int idColumn=-1;
	protected String idColumnId;
	private String name="";
	

	@Override
	public List<? extends ITabularItem> items() {
		return items;
	}

	@Override
	public int length() {
		return items.size();
	}

	@Override
	public List<? extends IColumn> columns() {
		return columns;
	}

	
	@SuppressWarnings("unchecked")
	public ITabularDataSet addColumn(String id, Function<ITabularItem, Object> func) {
		ComputableColumn computableColumn = new ComputableColumn(id, id, columns.size(), Object.class, func);
		((List<IColumn>)this.columns).add(computableColumn);
		return this;
	}

	@Override
	public ITabularItem get(int i) {
		return items.get(i);
	}

	@Override
	public ITabularDataSet removeColumn(IColumn cl) {
		this.columns.remove(cl);
		this.idColumn=columns.indexOf(getColumn(idColumnId));
		return this;
	}
	
	public void setIdColumn(String cl) {
		this.idColumnId=cl;
		this.idColumn=columns.indexOf(getColumn(cl));
	}

	public void setIdColumn(IColumn cl) {
		this.idColumnId=cl.id();
		this.idColumn=columns.indexOf(cl);
	}
	
	public IDataSetDelta compare(IDataSet ds) {
		if (!(ds instanceof ITabularDataSet)) {
			throw new IllegalArgumentException("Tabular datasets can only be compared to other tabular datasets");
		}
		ITabularDataSet d=ds.as(ITabularDataSet.class);
		
		LinkedHashSet<ITabularItem>additions=new LinkedHashSet<ITabularItem>();
		LinkedHashSet<ITabularItem>removals=new LinkedHashSet<ITabularItem>();
		LinkedHashSet<ITabularItem>changes=new LinkedHashSet<ITabularItem>();
		
		LinkedHashMap<String, ITabularItem> ids = getItemMap();
		
		d.items().forEach(t->{
			String id = t.id();
			if (!ids.containsKey(id)) {
				additions.add(t);
			}
			else {
				if (!ids.get(id).equals(t)) {
					changes.add(t);
				}
			}
			ids.remove(id);
		});
		removals.addAll(ids.values());
		
		return new BasicDelta(new BasicDataSetImpl(new ArrayList<ITabularItem>(additions), columns,this.idColumnId),
		new BasicDataSetImpl(new ArrayList<ITabularItem>(removals), columns,this.idColumnId),
		new BasicDataSetImpl(new ArrayList<ITabularItem>(changes), columns,this.idColumnId));
	}

	public LinkedHashMap<String, ITabularItem> getItemMap() {
		LinkedHashMap<String,ITabularItem>ids=new LinkedHashMap<String,ITabularItem>();
		for (ITabularItem t:items) {
			String id = t.id();
			ids.put(id, t);
		}
		return ids;
	}

	@Override
	public ITabularDataSet mergeBy(IColumn cln) {
		LinkedHashMap<Object, ArrayList<ITabularItem>> ids =new LinkedHashMap<Object, ArrayList<ITabularItem>>();
		this.items.forEach(i->{
			Object value = cln.getValue(i);
			ArrayList<ITabularItem> arrayList = ids.get(value);
			if (arrayList==null) {
				arrayList=new ArrayList<ITabularItem>();
				ids.put(value, arrayList);
			}
			arrayList.add(i);
		});
		ArrayList<BasicItem>items=new ArrayList<BasicItem>();
		int num=0;
		ArrayList<IColumn>newColumns=new ArrayList<IColumn>();
		for (Object o:ids.keySet()) {
			ArrayList<ITabularItem> arrayList = ids.get(o);
			Object[]newValues=new Object[this.columns.size()];
			int a=0;		
			for (IColumn c:this.columns) {
				if (c!=cln) {
					Object[]values=new Object[arrayList.size()];
					for (int i=0;i<arrayList.size();i++) {
						values[i]=c.getValue(arrayList.get(i));
					}
					newValues[a]=values;
				}
				else {
					newValues[a]=o;
				}
				a=a+1;
			}
			items.add(new BasicItem(num, newValues));			
		}
		for (IColumn c:this.columns) {
			c.clone();
		}
		return new BasicDataSetImpl(items, this.columns);
	}

	@Override
	public IItem item(int num) {
		return this.items.get(num);
	}

	@Override
	public String name() {
		return name;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IDataSet subDataSet(String string, List<? extends IItem> arrayList) {
		BasicDataSetImpl rs=(BasicDataSetImpl) this.clone();
		rs.items=(ArrayList)arrayList;
		rs.name=string;
		return rs;
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSettings(IVisualizerProto proto, Map<String, Object> parameters) {
		
	}

	@Override
	public List<DescriptionEntry> description() {
		ArrayList<DescriptionEntry>descriptions=new ArrayList<>();
		descriptions.add(new DescriptionEntry("Size", this.size()));
		descriptions.add(new DescriptionEntry("Columns", this.columns.stream().map(x->x.caption()).collect(Collectors.joining())));
		return descriptions;
	}

	@Override
	public List<ConversionAction> conversions() {
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITabularDataSet addColumn(IColumn func) {
		Column func2 = (Column)func;
		func2.owner=this;
		((List)this.columns).add(func2);
		return this;
	}
	
	@Override
	public ITabularDataSet clone(){
		try {
			BasicDataSetImpl clone = (BasicDataSetImpl) super.clone();
			clone.columns=new ArrayList<>(this.columns);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

}