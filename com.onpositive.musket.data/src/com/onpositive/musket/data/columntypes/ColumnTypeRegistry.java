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
		types.add(new TextColumnType("", "text", "Text"));
		types.add(new ImageColumnType("", "image", "Image"));
		types.add(new RLEMaskColumnType("", "rle", "RLE Mask"));
		types.add(new NumberColumn("", "number", "Number"));
		types.add(new ClassColumnType("", "class", "Class"));
		types.add(new IDColumnType("", "id", "Id"));
	}
	
	public static ColumnTypeRegistry getInstance() {
		if (instance==null) {
			instance=new ColumnTypeRegistry();
		}
		return instance;
	}
}
