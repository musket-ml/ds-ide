package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ImageRepresenter;

public class BinarySegmentationDataSet extends AbstractRLEImageDataSet<BinarySegmentationItem> implements IBinarySegmentationDataSet,Cloneable{

	
	
	public BinarySegmentationDataSet(ITabularDataSet base,IColumn image,IColumn rle,ImageRepresenter rep,int width,int height) {
		super(base,image,rle,width,height,rep);
		
	}
	
	public BinarySegmentationDataSet(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
	}

	@Override
	public Collection<BinarySegmentationItem> items() {
		if (items==null) {
			items=new ArrayList<>();
		    base.items().forEach(v->{
		    	items.add(new BinarySegmentationItem(this,v));
		    });
		}
		return items;
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		return new BinarySegmentationDataSetWithGroundTruth(base, imageColumn, rleColumn, representer, width, height, (ITabularDataSet) t2);
	}

	@Override
	protected String getKind() {
		return "Binary Segmentation";
	}
	
	
}