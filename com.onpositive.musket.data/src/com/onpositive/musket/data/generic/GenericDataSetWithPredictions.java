package com.onpositive.musket.data.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDColumnType;
import com.onpositive.musket.data.columntypes.ImageColumnType;
import com.onpositive.musket.data.columntypes.TextColumnType;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class GenericDataSetWithPredictions extends GenericDataSet implements IDataSetWithGroundTruth {

	private GenericDataSet predictions;

	public GenericDataSetWithPredictions(DataSetSpec spec, ITabularDataSet t1, GenericDataSet d1) {
		super(spec, t1);
		this.predictions = d1;
	}

	@Override
	public IItem getPrediction(int num) {
		return ((GenericItemWithPrediction) items().get(num)).getPrediction();
	}

	@Override
	public List<? extends IItem> items() {
		Collection<ColumnInfo> infos = this.getSpec().layout.infos();
		ArrayList<IColumn> stable = new ArrayList<>();
		for (ColumnInfo i : infos) {
			IColumn column = i.getColumn();
			if (i.preferredType() == IDColumnType.class || i.preferredType() == ImageColumnType.class
					|| i.preferredType() == TextColumnType.class) {
				// this is a potentinal id
				Collection<Object> values = new LinkedHashSet<>(column.uniqueValues());
				Collection<Object> values1 = new LinkedHashSet<>(
						predictions.tabularBase.getColumn(column.id()).uniqueValues());
				if (values.equals(values1)) {
					// this column seems identical
					stable.add(column);
				}
			}
		}
		if (stable.isEmpty()) {
			for (ColumnInfo i : infos) {
				IColumn column = i.getColumn();
				if (true) {
					// this is a potentinal id
					Collection<Object> values = column.values();
					Collection<Object> values1 = 
							predictions.tabularBase.getColumn(column.id()).values();
					if (values.equals(values1)) {
						// this column seems identical
						stable.add(column);
					}
				}
			}
		}
		LinkedHashMap<String, GenericItem> nitems = new LinkedHashMap<>();
		if (!stable.isEmpty()) {
			predictions.items().forEach(v -> {
				String string = id(stable, v);
				nitems.put(string, (GenericItem) v);
			});
		}
		if (items == null) {
			items = tabularBase.items().stream().map(x -> {
				GenericItem genericItem = nitems.get(id1(stable, x));
				return new GenericItemWithPrediction(this, x, genericItem);
			}).collect(Collectors.toList());
			;
		}
		return items;
	}

	protected String id1(ArrayList<IColumn> stable, IItem v) {
		StringBuilder bld = new StringBuilder();
		for (IColumn c : stable) {

			bld.append(c.getValueAsString((ITabularItem) v));
		}
		String string = bld.toString();
		return string;
	}

	protected String id(ArrayList<IColumn> stable, IItem v) {
		StringBuilder bld = new StringBuilder();
		GenericItem v1 = (GenericItem) v;
		for (IColumn c : stable) {

			bld.append(c.getValueAsString(v1.generic_base()));
		}
		String string = bld.toString();
		return string;
	}
}
