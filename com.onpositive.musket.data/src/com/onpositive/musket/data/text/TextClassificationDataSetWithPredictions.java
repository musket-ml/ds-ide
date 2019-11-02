package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.BinaryClassificationDataSetWithGroundTruth;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IMultiClassificationWithGroundTruth;
import com.onpositive.musket.data.images.MultiClassificationDataset;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;

public class TextClassificationDataSetWithPredictions extends TextClassificationDataSet implements IDataSetWithGroundTruth,IMultiClassificationWithGroundTruth{

	private TextClassificationDataSet predictions;

	public TextClassificationDataSetWithPredictions(ITabularDataSet base, IColumn textColumn,
			ArrayList<IColumn> clazzColumns,TextClassificationDataSet predictions) {
		super(base, textColumn, clazzColumns);
		this.predictions=predictions;
	}
	@Override
	public List items() {
		return super.items();
	}
	
	protected ArrayList<IItem> createItems() {
		ArrayList<IItem> items = new ArrayList<>();
		Map<String, IItem> itemMap = predictions.itemMap();
		base.items().forEach(v -> {
			items.add(new TextItemWithPrediction(this, v, (TextItem) itemMap.get(v.id())));
		});
		return items;
	}

	@Override
	public IBinaryClassificationDataSet forClass(String clazz) {
		ITabularDataSet filter = filter(clazz, base, clazzColumn.caption());
		TextClassificationDataSet filter1 = (TextClassificationDataSet) predictions.withIds(filter);
		return new TextClassificationDataSetWithPredictions(filter, this.textColumn, this.clazzColumns, filter1);
	}

	@Override
	public IItem getPrediction(int num) {
		return predictions.item(num);
	}

}
