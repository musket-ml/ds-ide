package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class MultiClassInstanceSegmentationDataSet extends MultiClassSegmentationDataSet{

	public MultiClassInstanceSegmentationDataSet(ITabularDataSet base2, IColumn image, IColumn rle, int width2,
			int height2, ImageRepresenter rep, IColumn clazzColumn) {
		super(base2, image, rle, width2, height2, rep, clazzColumn);

	}

	public MultiClassInstanceSegmentationDataSet(ITabularDataSet base, Map<String, Object> settings,
			ImageRepresenter rep) {
		super(base, settings, rep);
	}

	@Override
	protected MultiClassSegmentationItem createItem(LinkedHashMap<String, ArrayList<ITabularItem>> items, String k) {
		return new MultiClassInstanceSegmentationItem(k, this, items.get(k));
	}
	
	@Override
	protected String getKind() {
		return "Multi class instance segmentation";
	}
}
