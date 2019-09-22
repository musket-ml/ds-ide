package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.images.actions.BasicImageDataSetActions.ConversionAction;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;

public abstract class AbstractTextDataSet implements IDataSet, Cloneable {

	protected ITabularDataSet base;
	protected IColumn idColumn;

	public AbstractTextDataSet(ITabularDataSet base, IColumn textColumn, IColumn idColumn) {
		super();
		this.base = base;
		this.textColumn = textColumn;
		this.idColumn = idColumn;
	}

	protected IColumn textColumn;
	protected ArrayList<IItem> items ;
	protected Map<String, Object> settings = new LinkedHashMap<String, Object>();
	private String name = "";
	

	@Override
	public int length() {
		return items().size();
	}

	@Override
	public List<? extends IItem> items() {
		if (this.items == null) {
			this.items = this.createItems();
		}
		return this.items;
	}

	protected abstract ArrayList<IItem> createItems();

	@Override
	public IDataSetDelta compare(IDataSet d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IItem item(int num) {
		return items().get(num);
	}

	@Override
	public IVisualizerProto getVisualizer() {
		return new IVisualizerProto() {

			@Override
			public Parameter[] parameters() {
			
				return new Parameter[] {};
			}

			@Override
			public String name() {
				return "Text visualizer";
			}

			@Override
			public String id() {
				return "Text visualizer";
			}
		};
	}

	@Override
	public void setSettings(IVisualizerProto proto, Map<String, Object> parameters) {
		this.settings.clear();
		this.settings.putAll(settings);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IDataSet subDataSet(String string, List<? extends IItem> arrayList) {
		try {
			AbstractTextDataSet rs = (AbstractTextDataSet) this.clone();
			rs.items = (ArrayList) arrayList;
			rs.name = string;
			return rs;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<DescriptionEntry> description() {
		ArrayList<DescriptionEntry> result = new ArrayList<>();
		return result;
	}

	@Override
	public List<ConversionAction> conversions() {
		return new ArrayList<>();
	}

	public String name() {
		return this.name;
	}

	public abstract boolean isPositive(TextItem textItem);

	public abstract Object binaryLabel(TextItem textItem);
}
