package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class BinarySegmentationDataSet extends AbstractRLEImageDataSet<BinarySegmentationItem> implements IBinarySegmentationDataSet,Cloneable{
	
	public BinarySegmentationDataSet(DataSetSpec base,IColumn image,IColumn rle,int width,int height) {
		super(base,image,rle,width,height);
		
	}
	
	public BinarySegmentationDataSet(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
	}

	@Override
	public Collection<BinarySegmentationItem> items() {
		if (items==null) {
			items=new ArrayList<>();
		    tabularBase.items().forEach(v->{
		    	items.add(new BinarySegmentationItem(this,v));
		    });
		}
		return items;
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		return new BinarySegmentationDataSetWithGroundTruth(new DataSetSpec(tabularBase, representer), imageColumn, rleColumn, width, height, (ITabularDataSet) t2);
	}

	@Override
	protected String getKind() {
		return "Binary Segmentation";
	}

	@Override
	public String generatePythonString(String sourcePath,Object model) {
		return "image_datasets."+getPythonName()+"("+this.getDataSetArgs(sourcePath).stream().collect(Collectors.joining(","))+")";
	}

	protected String getPythonName() {
		return "BinarySegmentationDataSet";
	}

	protected  ArrayList<String> getDataSetArgs(String sourcePath) {
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add(getImageDirs());
		arrayList.add('"'+sourcePath+'"');
		arrayList.add('"'+getImageIdColumn()+'"');
		arrayList.add('"'+getRLEColumn()+'"');
		if (this.isRelativeRLE) {
			arrayList.add("isRel=True");
		}
		if (!this.widthFirst) {
			arrayList.add("rMask=False");
		}
		return arrayList;
	}

	@Override
	public List<ITabularItem> represents(IItem i) {
		BinarySegmentationItem ti=(BinarySegmentationItem) i;
		return Collections.singletonList(ti.item);
	}
	
}