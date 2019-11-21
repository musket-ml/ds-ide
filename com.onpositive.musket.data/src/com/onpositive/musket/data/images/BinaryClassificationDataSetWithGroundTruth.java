package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class BinaryClassificationDataSetWithGroundTruth extends BinaryClassificationDataSet implements IDataSetWithGroundTruth{

	protected ITabularDataSet predictions;

	public BinaryClassificationDataSetWithGroundTruth(ITabularDataSet base, IColumn image, IColumn clazz,
			ImageRepresenter rep, int width, int height,ITabularDataSet prediction) {
		super(base, image, clazz,  width,height, rep);		
		this.predictions=prediction;
	}
	@Override
	public Collection<BinaryClassificationItem> items() {
		Map<String, ITabularItem> itemMap = this.predictions.getItemMap();
		if (items==null) {
			items=new ArrayList<>();
		    tabularBase.items().forEach(v->{
		    	items.add(new BinaryClassificationItemWithGroundTruth(this,v,itemMap.get(v.id())));
		    });
		}
		return items;
	}

	

	@Override
	public IItem getPrediction(int num) {
		BinaryClassificationItemWithGroundTruth binarySegmentationItem = (BinaryClassificationItemWithGroundTruth) this.items.get(num);
		return binarySegmentationItem.getPrediction();
	}

}
