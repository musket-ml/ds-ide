package com.onpositive.musket.data.generic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;

@Display("dlf/generic_settings.dlf")
public class GenerateMusketWrapperSettings {


	protected ArrayList<ColumnInfo>allColumns=new ArrayList<>();
	
	
	protected ArrayList<ColumnCoding>inputColumns=new ArrayList<>();
	
	protected ArrayList<ColumnCoding>outputColumns=new ArrayList<>();

	
	protected ArrayList<ColumnInfo>allSelection;
	protected ArrayList<ColumnCoding>inputSelection;
	protected ArrayList<ColumnCoding>outputSelection;
	
	public void moveToInput() {
		if (allSelection==null) {
			return;
		}
		allColumns.removeAll(allSelection);
		inputColumns.addAll(allSelection.stream().map(x->new ColumnCoding(x)).collect(Collectors.toList()));
		//inputColumns.addAll(allSelection);
		allSelection.clear();
		ObjectChangeManager.markChanged(this);
		
	}
	public static class ColumnCoding{
		
		ColumnInfo column;
		
		public ColumnCoding(ColumnInfo column) {
			super();
			this.column = column;
			try {
			IColumnType newInstance = column.preferredType().newInstance();
			coder= newInstance.typeId(column.getColumn());
			}catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
		@RealmProvider(CodersRealmProvider.class)
		String coder;
		String group;
	}
	public static class CodersRealmProvider implements IRealmProvider<String>{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public IRealm<String> getRealm(IHasMeta arg0, Object arg1, Object arg2) {
			Realm<String> rs=new Realm<String>();
			rs.add("multi_class");
			rs.add("categorical_one_hot");
			rs.add("as_is");
			rs.add("binary");
			rs.add("rle");
			rs.add("image");
			rs.add("number");
			rs.add("normalized_number");
			return rs;
		}
		
	}
	
	public void moveToOutput() {
		if (allSelection==null) {
			return;
		}
		allColumns.removeAll(allSelection);
		outputColumns.addAll(allSelection.stream().map(x->new ColumnCoding(x)).collect(Collectors.toList()));
		allSelection.clear();
		ObjectChangeManager.markChanged(this);
	}
	public void concatenateSelected() {
		HashSet<String>used=new HashSet<>();
		for (ColumnCoding m:inputColumns) {
			used.add(m.group);
		}
		String mm="";
		for (int i=0;i<1000;i++) {
			mm=""+i;
			if (!used.contains(mm)) {
				break;
			}
		}
		if (inputSelection==null) {
			return;
		}
		for (ColumnCoding c:inputSelection) {
			c.group=mm;
			ObjectChangeManager.markChanged(c);
		}
	}
	public void concatenateSelected0() {
		HashSet<String>used=new HashSet<>();
		for (ColumnCoding m:outputColumns) {
			used.add(m.group);
		}
		String mm="";
		for (int i=0;i<1000;i++) {
			mm=""+i;
			if (!used.contains(mm)) {
				break;
			}
		}
		if (outputSelection==null) {
			return;
		}
		for (ColumnCoding c:outputSelection) {
			c.group=mm;
			ObjectChangeManager.markChanged(c);
		}
	}
	
	public void moveFromInputToAll() {
		if (inputSelection==null) {
			return;
		}
		inputColumns.removeAll(inputSelection);
		allColumns.addAll(inputSelection.stream().map(x->x.column).collect(Collectors.toList()));
		inputSelection.clear();
		ObjectChangeManager.markChanged(this);		
	}
	public void moveFromOutputToAll() {
		if (outputSelection==null) {
			return;
		}
		outputColumns.removeAll(outputSelection);
		allColumns.addAll(outputSelection.stream().map(x->x.column).collect(Collectors.toList()));		
		outputSelection.clear();
		ObjectChangeManager.markChanged(this);		
	}
}
