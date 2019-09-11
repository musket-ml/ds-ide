package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
}
