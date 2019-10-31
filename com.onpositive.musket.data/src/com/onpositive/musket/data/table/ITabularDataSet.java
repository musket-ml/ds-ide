package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.IDataSet;

public interface ITabularDataSet extends IDataSet,Cloneable {

	public List<? extends IColumn> columns();

	public default IColumn getColumn(String id) {
		for (IColumn c : columns()) {
			if (c.id().equals(id)) {
				return c;
			}
		}
		if (id.contains("|")) {
			String[] split = id.split("\\|");
			ArrayList<IColumn>aa=new ArrayList<>();
			boolean allMatch=true;
			for (String s:split) {
				IColumn z=getColumn(s);
				if (z!=null) {
					aa.add(z);
				}
				else {
					allMatch=false;
				}
			}
			if (allMatch) {
				return new ClassJoiningColumn(aa,aa.stream().map(x->x.id()).collect(Collectors.joining("|")), this);
			}
		}
		int lastIndexOf = id.lastIndexOf(':');
		if (lastIndexOf != -1) {
			Column column = (Column) getColumn(id.substring(0, lastIndexOf));
			if (column != null) {
				String[] split = column.id.split("_");
				String substring = id.substring(lastIndexOf + 1);
				int er2 = substring.indexOf('^');
				if (er2!=-1) {
					String fst=substring.substring(0,er2);
					String sec=substring.substring(er2+1);
					int sn1 = Integer.parseInt(fst);
					int sn2 = Integer.parseInt(sec);
					SubColumn subColumn = new SubColumn(column.id(), id, column,sn1,sn2);
					subColumn.owner=this;
					return subColumn;
				}
				else{
					int sn = Integer.parseInt(substring);
					SubColumn subColumn = new SubColumn(column.id(), split[sn], column.getNum(), String.class, sn);
					subColumn.owner=this;
					return subColumn;
				}
			}
		}
		return null;
	}

	Collection<? extends ITabularItem> items();
		

	public default ITabularDataSet filter(String column, Predicate<Object> values) {
		IColumn column2 = getColumn(column);
		ArrayList<ITabularItem> items = new ArrayList<ITabularItem>();
		this.items().forEach(v -> {
			if (values.test(column2.getValue(v))) {
				items.add(v);
			}
		});
		return new BasicDataSetImpl(items, this.columns()).as(ITabularDataSet.class);
	}
	public default ITabularDataSet map(String column, Function<Object,Object> values) {
		IColumn column2 = getColumn(column);
		ArrayList<ITabularItem> items = new ArrayList<ITabularItem>();
		List<? extends IColumn> columns = this.columns();
		if (column2 instanceof ComputableColumn) {
			ComputableColumn map = ((ComputableColumn) column2).map(values);
			List<IColumn> list = new ArrayList<>(columns);
			list.remove(column2);
			list.add((IColumn)map);
			this.items().forEach(v -> {
				BasicItem z=(BasicItem) v;
				Object[] clone = z.values.clone();
				items.add(new BasicItem(v.num(), clone));			
			});
			return new BasicDataSetImpl(items, list).as(ITabularDataSet.class);
		}
		int m=columns.indexOf(column2);
		this.items().forEach(v -> {
			BasicItem z=(BasicItem) v;
			Object[] clone = z.values.clone();
			clone[m]=values.apply(clone[m]);
			items.add(new BasicItem(v.num(), clone));			
		});		
		return new BasicDataSetImpl(items, columns).as(ITabularDataSet.class);
	}

	public ITabularDataSet addColumn(String id, Function<ITabularItem, Object> func);
	
	public ITabularDataSet addColumn(IColumn func);

	public ITabularDataSet removeColumn(IColumn cl);

	public default ITabularDataSet removeColumn(String cl) {
		return removeColumn(getColumn(cl));
	}

	public ITabularItem get(int i);

	public Map<String, ITabularItem> getItemMap();

	public ITabularDataSet mergeBy(IColumn cln);

	public default ITabularDataSet mergeBy(String cln) {
		return mergeBy(this.getColumn(cln));
	}

	public ITabularDataSet clone();

}