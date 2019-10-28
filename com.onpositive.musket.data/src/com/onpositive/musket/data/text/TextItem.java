package com.onpositive.musket.data.text;
import java.util.ArrayList;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.IBinaryClasificationItem;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.musket.data.images.MultiClassClassificationItem;
import com.onpositive.musket.data.table.ITabularItem;

public class TextItem implements ITextItem,IBinaryClasificationItem,IMulticlassClassificationItem{
	
	protected AbstractTextDataSet textDataSet;
	protected ITabularItem baseItem;
	
	public TextItem(AbstractTextDataSet textDataSet, ITabularItem baseItem) {
		super();
		this.textDataSet = textDataSet;
		this.baseItem = baseItem;
	}

	@Override
	public String id() {
		if (textDataSet.idColumn!=null) {
			return textDataSet.idColumn.getValueAsString(this.baseItem);
		}
		return this.baseItem.id();
	}

	@Override
	public IDataSet getDataSet() {
		return textDataSet;
	}

	@Override
	public String getText() {
		return textDataSet.textColumn.getValueAsString(baseItem);
	}
	public Object binaryLabel() {return textDataSet.binaryLabel(this);}
	
	@Override
	public boolean isPositive() {
		return textDataSet.isPositive(this);
	}
	@Override
	public ArrayList<String> classes() {
		String value = (String) ((TextClassificationDataSet)this.textDataSet).clazzColumn.getValue(baseItem);
		return MultiClassClassificationItem.splitByClass(value,this.textDataSet.labels);
	}
	
}
