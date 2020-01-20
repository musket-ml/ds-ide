package com.onpositive.musket.data.images;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class MultiClassSegmentationDataSetWithGrounTruth extends MultiClassSegmentationDataSet
		implements IDataSetWithGroundTruth {

	private MultiClassSegmentationDataSet predictionData;

	public MultiClassSegmentationDataSetWithGrounTruth(DataSetSpec base2, IColumn image, IColumn rle, int width2,
			int height2, IColumn clazzColumn, ITabularDataSet predictions) {
		super(base2, image, rle, width2, height2, clazzColumn);
		{
			parameters.put(BinarySegmentationDataSetWithGroundTruth.PREDICTION_ALPHA, MASK_ALPHA_DEFAULT);
		}
		this.predictionData = new MultiClassSegmentationDataSet(new DataSetSpec(predictions, base2.representer), image,
				rle, width2, height2, clazzColumn);
	}

	protected MultiClassSegmentationItem createItem(LinkedHashMap<String, ArrayList<ITabularItem>> items, String k) {

		return new MultiClassSegmentationItemWithPrediction(k, this, items.get(k));
	}

	protected void addExtraParameters(ArrayList<Parameter> rs) {
		Parameter alpha1 = new Parameter();
		alpha1.defaultValue = parameters.get(BinarySegmentationDataSetWithGroundTruth.PREDICTION_ALPHA).toString();
		alpha1.type = int.class;
		alpha1.name = BinarySegmentationDataSetWithGroundTruth.PREDICTION_ALPHA;
		rs.add(alpha1);
	}

	@Override
	public IItem getPrediction(int num) {
		return predictionData.getItem(this.item(num).id());
	}

	public IItem getPrediction(String id) {
		return predictionData.getItem(id);
	}

	@Override
	public IBinaryClassificationDataSet forClass(String clazz) {
		ITabularDataSet filter = filter(clazz, this.predictionData.tabularBase, clazzColumn.id());
		return (IBinaryClassificationDataSet) new BinarySegmentationDataSet(filter(clazz, this.tabularBase, clazzColumn.id()),
				this.getSettings(), representer).withPredictions(filter);
	}
}