package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.onpositive.musket.data.core.IDataSet;

public interface ITabularDataSet extends IDataSet {

	public List<? extends IColumn> columns();

	public default IColumn getColumn(String id) {
		for (IColumn c : columns()) {
			if (c.id().equals(id)) {
				return c;
			}
		}
		int lastIndexOf = id.lastIndexOf(':');
		if (lastIndexOf != -1) {
			Column column = (Column) getColumn(id.substring(0, lastIndexOf));
			if (column != null) {
				String[] split = column.id.split("_");
				String substring = id.substring(lastIndexOf + 1);
				int sn = Integer.parseInt(substring);
				SubColumn subColumn = new SubColumn(column.id(), split[sn], column.num, String.class, sn);
				subColumn.owner=this;
				return subColumn;
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

	public ITabularDataSet addColumn(String id, Function<ITabularItem, Object> func);

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

}