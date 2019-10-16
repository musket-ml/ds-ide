package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.Column;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.SubColumn;
import com.onpositive.musket.data.table.IColumnType.ColumnPreference;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;

public class ColumnLayout {

	
	public static class ColumnInfo {
		protected IColumn cln;

		protected HashMap<Class<? extends IColumnType>, ColumnPreference> prefs = new HashMap<>();

		public ColumnInfo(IColumn cln) {
			super();
			this.cln = cln;
		}

		public IColumn getColumn() {
			return cln;
		}

		public Class<? extends IColumnType> preferredType() {
			Class<? extends IColumnType> nb = null;
			for (Class<? extends IColumnType> c : prefs.keySet()) {
				ColumnPreference columnPreference = prefs.get(c);
				if (columnPreference == ColumnPreference.STRICT) {
					return c;
				}
				if (columnPreference == ColumnPreference.MAYBE) {
					nb = c;
				}
			}
			if (nb == null) {
				return ClassColumnType.class;
			}
			return nb;
		}
		
		@Override
		public String toString() {
			return cln.caption()+":"+preferredType().getAnnotation(Caption.class).value();
		}
	}

	protected LinkedHashMap<IColumn, ColumnInfo> infos = new LinkedHashMap<>();
	
	private ITabularDataSet newDataSet;

	public ITabularDataSet getNewDataSet() {
		return newDataSet;
	}

	public ColumnLayout(List<IColumn> clns, DataProject prj, IQuestionAnswerer answerer) {
		ArrayList<IColumn>mm=new ArrayList<>();
		boolean foundInterestingColumns=false;
		for (IColumn v : clns) {
			if (v.id().contains("_")) {
				String[] split = v.id().split("_");
				int sn = 0;
				for (String s : split) {
					Column c = (Column) v;
					SubColumn subColumn = new SubColumn(v.id(), s, c, sn);
					if (prj.getRepresenter().like(subColumn)) {
						foundInterestingColumns=true;
					}
					mm.add(subColumn);					
					sn = sn + 1;
				}
			}
			else{
				mm.add(v.clone());
			}
		}
		if (foundInterestingColumns) {
			clns=mm;
			BasicDataSetImpl newDs = new BasicDataSetImpl((ArrayList<? extends ITabularItem>) clns.get(0).owner().items(), mm);
			this.newDataSet=newDs;
		}
		else {
			this.newDataSet=clns.get(0).owner();
		}
		clns.forEach(v -> {
			ColumnInfo columnInfo = new ColumnInfo(v);
			infos.put(v, columnInfo);
			fillInfo(columnInfo, prj, answerer);

		});
	}

	private void fillInfo(ColumnInfo columnInfo, DataProject prj, IQuestionAnswerer answerer) {
		ColumnTypeRegistry.getInstance().getTypes().forEach(v -> {
			columnInfo.prefs.put(v.getClass(), v.is(columnInfo.cln, prj, answerer));
		});
	}

	public Collection<ColumnInfo> infos() {
		return this.infos.values();
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		this.infos.values().forEach(v -> {
			bld.append(v.cln.caption() + " - ");
			v.prefs.keySet().forEach(va -> {
				ColumnPreference columnPreference = v.prefs.get(va);
				if (columnPreference != ColumnPreference.NEVER && columnPreference != null) {
					bld.append(va.getSimpleName() + ": " + columnPreference.name() + " ");
				}
			});
			bld.append(System.lineSeparator());
		});
		return bld.toString();
	}
}
