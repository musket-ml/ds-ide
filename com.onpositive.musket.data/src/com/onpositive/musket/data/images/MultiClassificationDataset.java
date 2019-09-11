package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class MultiClassificationDataset extends BinaryClassificationDataSet implements IMulticlassClassificationDataSet{

	protected ArrayList<Object> classes;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MultiClassificationDataset(ITabularDataSet base2, IColumn image, IColumn clazzColumn, int width2,
			int height2, ImageRepresenter rep) {
		super(base2, image, clazzColumn, width2, height2, rep);
		Collection<Object> values = clazzColumn.values();
		classes = new ArrayList(new LinkedHashSet<>(values));
		try {
			Collections.sort((List) classes);
		} catch (Exception e) {
		}
	}

	public MultiClassificationDataset(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<MultiClassClassificationItem> items() {
		return (Collection<MultiClassClassificationItem>) super.items();
	}
	
	protected BinaryClassificationItem createItem(ITabularItem v){return new MultiClassClassificationItem(this,v);}

	@Override
	public boolean isExclusive() {
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> classNames() {
		return (List)classes;
	}
	@Override
	protected String getKind() {
		return "Multiclass classification";
	}

}