package com.onpositive.musket.data.images;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IHasLabels;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class MultiClassificationDataset extends BinaryClassificationDataSet implements IMulticlassClassificationDataSet,IHasLabels{
	
	public MultiClassificationDataset(ITabularDataSet base2, IColumn image, IColumn clazzColumn, int width2,
			int height2, ImageRepresenter rep) {
		super(base2, image, clazzColumn, width2, height2, rep);
		initClasses(clazzColumn);		
	}

	public MultiClassificationDataset(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<MultiClassClassificationItem> items() {
		return (Collection<MultiClassClassificationItem>) super.items();
	}
	
	@Override
	public IDataSet withPredictions(IDataSet t2) {
		MultiClassificationDataSetWithGroundTruth multiClassificationDataSetWithGroundTruth = new MultiClassificationDataSetWithGroundTruth(base, imageColumn, clazzColumn, representer, width, height, (ITabularDataSet) t2);
		if (this.labels!=null) {
			multiClassificationDataSetWithGroundTruth.labels=this.labels;
		}
		return multiClassificationDataSetWithGroundTruth;		
	}
	
	protected BinaryClassificationItem createItem(ITabularItem v){return new MultiClassClassificationItem(this,v);}

	@Override
	public boolean isExclusive() {
		return !multi;
	}
	
	protected String getPythonName() {
		if (this.isExclusive()) {
			return "CategoryClassificationDataSet";
		}
		return "MultiClassClassificationDataSet";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> classNames() {
		return (List)classes;
	}
	@Override
	protected String getKind() {
		return "Multiclass classification";
	}


	@Override
	public IBinaryClassificationDataSet forClass(String clazz) {
		ITabularDataSet filter = filter(clazz, base,clazzColumn.caption());
		return new BinaryClassificationDataSet(filter, this.getSettings(), representer);
	}

	protected static ITabularDataSet filter(String clazz, ITabularDataSet base2,String clazzColumn) {
		ITabularDataSet filter = base2.map(clazzColumn, x->{
			return MultiClassClassificationItem.splitByClass(x.toString(),null).contains(clazz)?"1":"0";
		});
		return filter;
	}

	public void setLabels(LabelsSet labelsSet) {
		this.labels=labelsSet;
	}

	@Override
	public LabelsSet labels() {
		return labels;
	}

}