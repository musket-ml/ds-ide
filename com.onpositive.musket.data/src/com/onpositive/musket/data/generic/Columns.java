package com.onpositive.musket.data.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;

@Display("dlf/columns.dlf")
public class Columns {

	protected String value="";
	
	List<ColumnInfo> columns=new ArrayList<ColumnInfo>();
	
	
	private List<ColumnInfo> selectedColumns=new ArrayList<ColumnInfo>();

	@RealmProvider(expression="$.columns")
	public List<ColumnInfo> getSelectedColumns() {
		return selectedColumns;
	}

	public void setSelectedColumns(List<ColumnInfo> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}

	public Columns(List<ColumnInfo> columns, Object value2) {
		this.columns=columns;
		if (value2==null) {
			value2="";
		}
		String mn=value2.toString();
		String[] split = mn.split(",");
		if (split.length==0||mn.isEmpty()) {
			selectedColumns.addAll(columns);
			//columns.clear();
		}
		else {
			HashSet<String>mna=new HashSet<>(Arrays.asList(split));
			for (ColumnInfo m:columns) {
				mna.contains(m.getColumn().caption());
				selectedColumns.add(m);
			}
			//columns.removeAll(selectedColumns);
		}
	}
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
