package com.onpositive.musket.data.generic;

import java.util.ArrayList;

import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/generic_settings.dlf")
public class GenerateMusketWrapperSettings {


	protected ArrayList<ColumnInfo>allColumns=new ArrayList<>();
	
	
	protected ArrayList<ColumnInfo>inputColumns=new ArrayList<>();
	
	protected ArrayList<ColumnInfo>outputColumns=new ArrayList<>();

	
	protected ArrayList<ColumnInfo>allSelection;
	protected ArrayList<ColumnInfo>inputSelection;
	protected ArrayList<ColumnInfo>outputSelection;
	
	public void moveToInput() {
		if (allSelection==null) {
			return;
		}
		allColumns.removeAll(allSelection);
		inputColumns.addAll(allSelection);
		allSelection.clear();
		ObjectChangeManager.markChanged(this);
		
	}
	public void moveToOutput() {
		if (allSelection==null) {
			return;
		}
		allColumns.removeAll(allSelection);
		outputColumns.addAll(allSelection);
		allSelection.clear();
		ObjectChangeManager.markChanged(this);
	}
	
	public void moveFromInputToAll() {
		if (inputSelection==null) {
			return;
		}
		inputColumns.removeAll(inputSelection);
		allColumns.addAll(inputSelection);
		inputSelection.clear();
		ObjectChangeManager.markChanged(this);		
	}
	public void moveFromOutputToAll() {
		if (outputSelection==null) {
			return;
		}
		outputColumns.removeAll(outputSelection);
		allColumns.addAll(outputSelection);
		outputSelection.clear();
		ObjectChangeManager.markChanged(this);		
	}
}
