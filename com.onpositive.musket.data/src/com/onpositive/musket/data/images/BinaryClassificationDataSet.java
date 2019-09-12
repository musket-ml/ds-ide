package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class BinaryClassificationDataSet extends AbstractImageDataSet<BinaryClassificationItem> implements IBinaryClassificationDataSet{

	protected IColumn clazzColumn;

	public BinaryClassificationDataSet(ITabularDataSet base2, IColumn image, IColumn clazzColumn, int width2, int height2,
			ImageRepresenter rep) {
		super(base2, image, width2, height2, rep);
		this.clazzColumn=clazzColumn;
		this.getSettings().put(MultiClassSegmentationDataSet.CLAZZ_COLUMN, this.clazzColumn.id());
	}

	public BinaryClassificationDataSet(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
		this.clazzColumn=base.getColumn(settings.get(MultiClassSegmentationDataSet.CLAZZ_COLUMN).toString());		
	}

	@Override
	public Collection<? extends IBinaryClasificationItem> items() {
		if (items==null) {
			items=new ArrayList<>();
		    base.items().forEach(v->{
		    	items.add(createItem(v));
		    });
		}
		return items;		
	}

	protected BinaryClassificationItem createItem(ITabularItem v){return new BinaryClassificationItem(this,v);}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		return new BinaryClassificationDataSetWithGroundTruth(base, imageColumn, clazzColumn, representer, width, height, (ITabularDataSet) t2);		
	}

	@Override
	protected String getKind() {
		return "Binary classification";
	}
	@Override
	public String generatePythonString(String sourcePath) {
		return "image_datasets."+getPythonName()+"("+this.getDataSetArgs(sourcePath).stream().collect(Collectors.joining(","))+")";
	}

	protected String getPythonName() {
		return "BinaryClassificationDataSet";
	}

	protected  ArrayList<String> getDataSetArgs(String sourcePath) {
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add(getImageDirs());
		arrayList.add('"'+sourcePath+'"');
		arrayList.add('"'+getImageIdColumn()+'"');
		arrayList.add('"'+clazzColumn.caption()+'"');		
		return arrayList;
	}
}
