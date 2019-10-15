package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Map;

import com.onpositive.musket.data.columntypes.ClassColumnType;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.musket.data.columntypes.TextColumnType;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.IColumnType.ColumnPreference;
import com.onpositive.musket.data.text.TextClassificationDataSet;

public class TextDataSetFactories implements IDataSetFactory{

	public static IDataSet create(ITabularDataSet t, IColumn textColumn,IQuestionAnswerer question_answerer) {
		ArrayList<IColumn>columns=new ArrayList<>(t.columns());
		columns.remove(textColumn);
		ArrayList<IColumn> findClassColumn = findClassColumns(columns);
		
		if (findClassColumn.size()>0) {
			return new TextClassificationDataSet(t, textColumn, findClassColumn);
		}
		return null;
	}
	protected static ArrayList<IColumn> findClassColumns(ArrayList<IColumn> arrayList) {
		ArrayList<IColumn> clazzColumns = new ArrayList<>();
		for (IColumn m : arrayList) {
			if (ClassColumnType.check(m)!=ColumnPreference.NEVER) {
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
		if(strictColumn!=null) {
			return 1;
		}
		return 0;
	}
	@Override
	public IDataSet create(DataSetSpec spec, Map<String, Object> options) {
		IColumn strictColumn = spec.getStrictColumn(TextColumnType.class);
		return create(spec.base(), strictColumn,spec.answerer);
	}
}
