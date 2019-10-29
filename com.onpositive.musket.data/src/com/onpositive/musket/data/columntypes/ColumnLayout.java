package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.core.IProgressMonitor;
import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.Column;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.IColumnType.ColumnPreference;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.SubColumn;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

public class ColumnLayout {

	public static class ColumnInfo {
		protected IColumn cln;

		protected HashMap<Class<? extends IColumnType>, ColumnPreference> prefs = new HashMap<>();

		public ArrayList<SubColumn> subColumns;

		public ColumnInfo(IColumn cln) {
			super();
			this.cln = cln;
		}

		public ColumnInfo(IColumn cln, Map<Object, Object> options) {
			this.cln = cln;
			Map<String, String> str = (Map<String, String>) options.get("preferences");
			if (str != null) {
				for (IColumnType t : ColumnTypeRegistry.getInstance().getTypes()) {
					this.prefs.put(t.getClass(), ColumnPreference.valueOf(str.get(t.getClass().getName())));
				}
			}

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
				return NumberColumn.class;
			}
			return nb;
		}

		@Override
		public String toString() {
			return cln.caption() + ":" + preferredType().getAnnotation(Caption.class).value();
		}

		public Object toOptions() {
			HashMap<String, Object> info = new HashMap<>();
			info.put("id", this.cln.caption());
			HashMap<String, Object> vli = new HashMap<>();
			for (Class<? extends IColumnType> c : prefs.keySet()) {
				ColumnPreference columnPreference = prefs.get(c);
				vli.put(c.getName(), columnPreference.name());
			}
			info.put("preferences", vli);
			return info;
		}
	}

	protected LinkedHashMap<IColumn, ColumnInfo> infos = new LinkedHashMap<>();

	private ITabularDataSet newDataSet;

	public ITabularDataSet getNewDataSet() {
		return newDataSet;
	}

	public ColumnLayout(List<IColumn> clns, DataProject prj, IQuestionAnswerer answerer, IProgressMonitor monitor) {
		ArrayList<IColumn> mm = new ArrayList<>();
		boolean foundInterestingColumns = false;
		for (IColumn v : clns) {
			boolean onProgress = monitor.onProgress("Analizing dataset layout:" + v.id(), 1);
			if (!onProgress) {
				return;
			}
			if (v.id().contains("_")) {
				String[] split = v.id().split("_");
				int sn = 0;
				for (String s : split) {
					Column c = (Column) v;
					SubColumn subColumn = new SubColumn(v.id(), s, c, sn);
					if (prj.getRepresenter().like(subColumn)) {
						foundInterestingColumns = true;
					}
					mm.add(subColumn);
					sn = sn + 1;
				}
			} else {
				mm.add(v.clone());
			}
		}
		if (foundInterestingColumns) {
			clns = mm;
			BasicDataSetImpl newDs = new BasicDataSetImpl(
					(ArrayList<? extends ITabularItem>) clns.get(0).owner().items(), mm);
			this.newDataSet = newDs;
		} else {
			this.newDataSet = clns.get(0).owner();
		}

		clns.forEach(v -> {
			ColumnInfo columnInfo = new ColumnInfo(v);
			infos.put(v, columnInfo);
			fillInfo(columnInfo, prj, answerer);
		});
		new ArrayList<>(infos.values()).forEach(v -> {
			if (v.subColumns != null) {
				// we need to do split

				Class<? extends IColumnType> preferredType = v.preferredType();
				if (newDataSet == v.cln.owner()) {
					BasicDataSetImpl newDs = new BasicDataSetImpl(
							(ArrayList<? extends ITabularItem>) v.cln.owner().items(), mm);
					this.newDataSet = newDs;
				}
				this.newDataSet.removeColumn(v.cln);

				for (IColumn c : v.subColumns) {
					this.newDataSet.addColumn(c);
					ColumnInfo columnInfo = new ColumnInfo(c);
					columnInfo.prefs = v.prefs;
					columnInfo.subColumns = null;
					infos.put(c, columnInfo);
				}
				infos.remove(v.cln);

			}
		});
	}

	public ColumnLayout(DataSetSpec spec, Object object) {
		ArrayList<Object> o1 = (ArrayList<Object>) object;
		this.newDataSet = spec.tb;
		for (Object z : o1) {
			Map<Object, Object> om = (Map<Object, Object>) z;
			IColumn column = spec.tb.getColumn(om.get("id").toString());
			this.infos.put(column, new ColumnInfo(column, om));
		}
	}

	private void fillInfo(ColumnInfo columnInfo, DataProject prj, IQuestionAnswerer answerer) {
		ColumnTypeRegistry.getInstance().getTypes().forEach(v -> {
			if (v instanceof ISmartColumnType) {
				SmartColumnPref is2 = ((ISmartColumnType) v).is2(columnInfo.cln, prj, answerer);

				columnInfo.prefs.put(v.getClass(), is2.preference);
				if (is2.preference == ColumnPreference.STRICT) {
					if (is2.columns != null) {
						columnInfo.subColumns = is2.columns;
					}
				}
			} else {
				columnInfo.prefs.put(v.getClass(), v.is(columnInfo.cln, prj, answerer));
			}
		});
	}

	public Collection<ColumnInfo> infos() {
		return this.infos.values();
	}

	public Object toOptions() {
		ArrayList<Object> opts = new ArrayList<>();
		this.infos.values().forEach(v -> {
			opts.add(v.toOptions());
		});
		return opts;
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
