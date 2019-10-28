package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class MultiClassInstanceSegmentationDataSet extends MultiClassSegmentationDataSet{

	public MultiClassInstanceSegmentationDataSet(DataSetSpec base2, IColumn image, IColumn rle, int width2,
			int height2, ImageRepresenter rep, IColumn clazzColumn) {
		super(base2, image, rle, width2, height2,  clazzColumn);

	}

	public MultiClassInstanceSegmentationDataSet(ITabularDataSet base, Map<String, Object> settings,
			ImageRepresenter rep) {
		super(base, settings, rep);
		this.getSettings().put(AbstractRLEImageDataSet.CLAZZ, this.getClass().getName());
	}

	@Override
	protected MultiClassSegmentationItem createItem(LinkedHashMap<String, ArrayList<ITabularItem>> items, String k) {
		return new MultiClassInstanceSegmentationItem(k, this, items.get(k));
	}
	
	@Override
	protected String getKind() {
		return "Multi class instance segmentation";
	}
	
	public String generatePythonString(String sourcePath) {
		return "image_datasets."+getPythonName()+"("+this.getDataSetArgs(sourcePath).stream().collect(Collectors.joining(","))+")";
	}
	
	protected String getPythonName() {
		return "InstanceSegmentationDataSet";
	}
}
