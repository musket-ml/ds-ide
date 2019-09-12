package com.onpositive.musket.data.images;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class BinaryInstanceSegmentationDataSet extends  AbstractRLEImageDataSet<BinaryInstanceSegmentationItem> implements IDataSet,IBinarySegmentationDataSet,Cloneable,IInstanceSegmentationDataSet{
	
	public BinaryInstanceSegmentationDataSet(ITabularDataSet base,IColumn image,IColumn rle,ImageRepresenter rep,int width,int height) {
		super(base,image,rle,width,height,rep);		
	}
	
	public BinaryInstanceSegmentationDataSet(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
	}

	@Override
	public Collection<BinaryInstanceSegmentationItem> items() {
		if (items==null) {
			items=new ArrayList<BinaryInstanceSegmentationItem>();
			LinkedHashMap<String, ArrayList<ITabularItem>>items=new LinkedHashMap<>();
		    base.items().forEach(v->{
		    	String value = imageColumn.getValueAsString(v);
		    	ArrayList<ITabularItem> arrayList = items.get(value);
		    	if (arrayList==null) {
		    		arrayList=new ArrayList<>();
		    		items.put(value, arrayList);
		    	}
		    	arrayList.add(v);
		    	
		    });
		    items.keySet().forEach(k->{
		    	BinaryInstanceSegmentationItem bi=new BinaryInstanceSegmentationItem(k, this, items.get(k));
		    	this.items.add(bi);
		    });
		}
		return items;
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getKind() {
		return "Binary Instance Segmentation";
	}

	@Override
	public String generatePythonString(String sourcePath) {
		throw new UnsupportedOperationException();
	}
	
}
