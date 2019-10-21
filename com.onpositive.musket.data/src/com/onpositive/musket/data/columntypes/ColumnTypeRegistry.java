package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.musket.data.table.IColumnType;

public class ColumnTypeRegistry {

	
	protected ArrayList<IColumnType>types=new ArrayList<>();
	
	public List<IColumnType>getTypes(){
		return types;
	}
	
	private static ColumnTypeRegistry instance;
	
	public ColumnTypeRegistry() {
		types.add(new TextColumnType());
		types.add(new ImageColumnType());
		types.add(new RLEMaskColumnType());
		types.add(new NumberColumn());
		types.add(new ClassColumnType());
		types.add(new IDColumnType());
	}
	
	public static ColumnTypeRegistry getInstance() {
		if (instance==null) {
			instance=new ColumnTypeRegistry();
		}
		return instance;
	}
}
