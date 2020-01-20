package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IHasLabels;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class MultiClassificationDataSetWithGroundTruth extends BinaryClassificationDataSetWithGroundTruth implements IMultiClassificationWithGroundTruth ,IHasLabels{

	public MultiClassificationDataSetWithGroundTruth(ITabularDataSet base, IColumn image, IColumn clazz,
			ImageRepresenter rep, int width, int height, ITabularDataSet prediction) {
		super(base, image, clazz, rep, width, height, prediction);
		initClasses(clazzColumn);
	}

	@Override
	public boolean isExclusive() {
		return !this.multi;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> classNames() {
		return (List)classes;
	}

	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection items() {
		Map<String, ITabularItem> itemMap = this.predictions.getItemMap();
		if (items==null) {
			items=new ArrayList<>();
		    tabularBase.items().forEach(v->{
		    	items.add(new MultiClassificationItemWithGroundTruth(this,v,itemMap.get(v.id())));
		    });
		}
		return items;
	}
	@Override
	protected String getKind() {
		return "Multiclass classification";
	}

	@Override
	public IBinaryClassificationDataSet forClass(String clazz) {
		ITabularDataSet filter = MultiClassificationDataset.filter(clazz, tabularBase,this.clazzColumn.caption());
		ITabularDataSet pred = predictions.withIds(filter);
		return new BinaryClassificationDataSetWithGroundTruth(filter,imageColumn,clazzColumn,representer,width,height,pred);
	}

	@Override
	public void setLabels(LabelsSet labelsSet) {
		this.labels=labelsSet;
	}

	@Override
	public LabelsSet labels() {
		return labels;
	}
}
