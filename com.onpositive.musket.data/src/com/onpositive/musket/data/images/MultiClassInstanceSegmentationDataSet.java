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
	
	public String generatePythonString(String sourcePath,Object model) {
		String classesString = "["+String.join(", ", this.classes.stream().map(x->"\""+x+"\"").collect(Collectors.toList()))+"]";
		Object misaiObject = this.getSettings().get(AbstractRLEImageDataSet.MASK_IS_SAME_AS_IMAGE);
		boolean misai = misaiObject != null && misaiObject instanceof Boolean && (Boolean)misaiObject;
		
		StringBuilder bld = new StringBuilder();
		
		bld.append("image_datasets.").append(getPythonName()).append("(");
		bld.append(this.getDataSetArgs(sourcePath).stream().collect(Collectors.joining(",")));
		
		if(!misai) {
			bld.append(", maskShape=(").append(this.height).append(",").append(this.width).append(")");
		}
		bld.append(", classes=").append(classesString).append(")");
		String result = bld.toString();
		return result;
	}
	
	protected String getPythonName() {
		return "InstanceSegmentationDataSet";
	}
}
