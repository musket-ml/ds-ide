package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;

import com.onpositive.musket.data.table.IColumnType.ColumnPreference;
import com.onpositive.musket.data.table.SubColumn;

public class SmartColumnPref {

	public final ColumnPreference preference;
	public final ArrayList<SubColumn>columns;
	
	public SmartColumnPref(ColumnPreference preference, ArrayList<SubColumn> columns) {
		super();
		this.preference = preference;
		this.columns = columns;
	}
	
}
