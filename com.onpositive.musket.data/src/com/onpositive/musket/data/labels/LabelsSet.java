package com.onpositive.musket.data.labels;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.FileDialog;

import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.BasicItem;
import com.onpositive.musket.data.table.Column;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.utils.MusketFileReader;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/labels.dlf")
public class LabelsSet {

	protected ArrayList<LabelItem> items = new ArrayList<>();
	private ITabularDataSet dataset;
	private boolean isOk;

	public LabelsSet(ITabularDataSet t, ArrayList<String> labels) {
		List<? extends IColumn> columns = t.columns();
		IColumn labelColumn = null;
		IColumn clazzColumn = null;
		IColumn parent=null;
		for (IColumn c : columns) {
			ArrayList<Object> uniqueValues = c.uniqueValues();
			if (new HashSet<>(uniqueValues).containsAll(new HashSet<>(labels))) {
				clazzColumn = c;
				isOk = true;
				continue;
			} else {
				labelColumn = c;
			}
			if (c.caption().contains("parent")) {
				parent=c;
			}
		}
		fillIItems(t, labelColumn, clazzColumn,parent);
		this.dataset = t;
	}

	public LabelsSet(ArrayList<String> labels) {
		labels.forEach(v -> {
			LabelItem labelItem = new LabelItem();
			labelItem.clazz = v;
			labelItem.label = v + " label";
			this.items.add(labelItem);

		});
		toTabular();
	}

	protected void toTabular() {
		ArrayList<ITabularItem> items = new ArrayList<>();
		this.items.forEach(labelItem -> {
			items.add(new BasicItem(null,0, new String[] { labelItem.clazz, labelItem.label }));
		});
		ArrayList<IColumn> cs = new ArrayList<>();
		cs.add(new Column("Clazz", "Clazz", 0, String.class));
		cs.add(new Column("Label", "Label", 1, String.class));
		dataset = new BasicDataSetImpl(items, cs);
	}

	public ITabularDataSet getData() {
		this.toTabular();
		return this.dataset;
	}

	protected void fillIItems(ITabularDataSet t, IColumn labelColumn, IColumn clazzColumn, IColumn parent) {
		HashMap<String, String>parents=new HashMap<>();
		HashMap<String, String>classes=new HashMap<>();
		t.items().forEach(v -> {
			LabelItem labelItem = new LabelItem();
			labelItem.clazz = clazzColumn.getValueAsString(v);
			String valueAsString = labelColumn.getValueAsString(v);
			if (parent!=null) {
				String value = parent.getValueAsString(v);
				if (!value.isEmpty()) {
					parents.put(labelItem.clazz, value);
				}
			}
			
			if (valueAsString.isEmpty()) {
				valueAsString=labelItem.clazz;
			}
			labelItem.label = valueAsString;
			classes.put(labelItem.clazz, labelItem.label);
			items.add(labelItem);
		});
		if (parent!=null) {
			for (LabelItem l:items) {
				String cl=l.clazz;
				while (parents.containsKey(cl)){
					String string = parents.get(cl);
					String string2 = classes.get(string);
					
					cl=string;
					if (string2!=null) {
						l.label=string2+"->"+l.label;
					}
				}
			}
		}
	}

	protected HashMap<String, String> clazzToLabel;

	public String map(String clazzName) {
		synchronized (this) {
			if (clazzToLabel == null) {
				clazzToLabel = new LinkedHashMap<String, String>();
				items.forEach(v -> {
					clazzToLabel.put(v.clazz, v.label);
				});
			}

		}

		if (clazzToLabel.containsKey(clazzName))

		{
			return clazzToLabel.get(clazzName);
		}
		return clazzName;
	}

	public boolean isOk() {
		return this.isOk;
	}
	
	@Caption("Import...")
	public void importLabels() {
		
		FileDialog fd = new FileDialog(org.eclipse.swt.widgets.Display.getCurrent().getActiveShell());
		String fPath = fd.open();
		File f = new File(fPath);
		if(!f.exists()) {
			return;
		}
		String str = MusketFileReader.readStringFile(f);
		List<String> lst = Arrays.asList(str.split("\n")).stream().map(x->x.trim()).collect(Collectors.toList());
		if (clazzToLabel == null) {
			clazzToLabel = new LinkedHashMap<String, String>();
		}
		for(int i = 0 ; i < lst.size() ; i++ ) {
			this.clazzToLabel.put(""+i,lst.get(i));
		}
		for(LabelItem l : this.items) {
			String lab = this.clazzToLabel.get(""+l.getClazz());
			if(lab!= null) {
				l.setLabel(lab);
			}
		}
		ObjectChangeManager.markChanged(this.items.toArray());		
	}
	
	public int size() {
		return this.items.size();
	}

	public ArrayList<LabelItem> getItems() {
		return items;
	}
	
}
