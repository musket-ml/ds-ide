package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.columntypes.BasicColumn;
import com.onpositive.musket.data.columntypes.ClassColumnType;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.musket.data.columntypes.TextColumnType;
import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.musket.data.table.ClassColumnsOptimizer;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IHasLabels;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.IColumnType.ColumnPreference;
import com.onpositive.musket.data.table.ImageDataSetFactories;

public class TextDataSetFactories implements IDataSetFactory {


	protected static ArrayList<IColumn> findClassColumns(ArrayList<IColumn> arrayList) {
		ArrayList<IColumn> clazzColumns = new ArrayList<>();
		for (IColumn m : arrayList) {
			if (ClassColumnType.check(m) != ColumnPreference.NEVER) {
				clazzColumns.add(m);
			}
		}
		return clazzColumns;
	}

	@Override
	public String caption() {
		return "Text dataset factories";
	}

	@Override
	public double estimate(DataSetSpec parameterObject) {
		IColumn strictColumn = parameterObject.getStrictColumn(TextColumnType.class);
		int cnt = 0;
		for (ColumnInfo i : parameterObject.layout.infos()) {
			if (i.preferredType() == TextColumnType.class) {
				cnt++;
			}
			
		}
		IColumn strictColumn2 = parameterObject.getStrictColumn(BasicColumn.class);
		if (strictColumn2!=null) {
			
		}
		if (cnt > 1) {
			return 0;
		}
		if (strictColumn != null) {
			return 1;
		}
		return 0;
	}

	@Override
	public IDataSet create(DataSetSpec spec, Map<String, Object> options) {
		if (options!=null) {
			TextClassificationDataSet textClassificationDataSet = new TextClassificationDataSet(spec.tb,options);
			Object object2 = options.get(ImageDataSetFactories.LABELS_PATH);
			if (object2 != null ) {
				IDataSet load = DataSetIO.load("file://" + object2.toString());
				ITabularDataSet as = load.as(ITabularDataSet.class);
				if (as != null) {
					IHasLabels hl = (IHasLabels) textClassificationDataSet;
					LabelsSet labelsSet = new LabelsSet(as, (ArrayList<String>) new ArrayList<>(hl.classNames()));
					if (labelsSet.isOk()) {
						hl.setLabels(labelsSet);
					}
				}
			}
			return textClassificationDataSet;
		}
		IColumn strictColumn = spec.getStrictColumn(TextColumnType.class);
		Collection<ColumnInfo> infos = spec.layout.infos();
		LinkedHashSet<IColumn>allClasses=new LinkedHashSet<>();
		for (ColumnInfo i:infos) {
			if(i.preferredType()==ClassColumnType.class) {
				allClasses.add(i.getColumn());
				
			}
		}		
		if (allClasses.isEmpty()) {
			for (ColumnInfo i:infos) {
				if(i.getPrefs().get(ClassColumnType.class)!=ColumnPreference.NEVER) {
					allClasses.add(i.getColumn());
					
				}
			}
			if (allClasses.isEmpty()) {
				return null;
			}
		}
		if (allClasses.size()>1) {
			boolean askQuestion = spec.answerer.askQuestion("We have detected multiple classification columns, please select columns that you would like to use?", 
					allClasses);
			if (!askQuestion||allClasses.isEmpty()) {
				return null;
			}
		}
		List<IColumn> optimize = ClassColumnsOptimizer.optimize(allClasses, spec.answerer);
		TextClassificationDataSet textClassificationDataSet = new TextClassificationDataSet(spec.tb, strictColumn, new ArrayList<>(optimize));
		ImageDataSetFactories.trySetupLabels(spec, textClassificationDataSet);
		return textClassificationDataSet;		
	}
}
