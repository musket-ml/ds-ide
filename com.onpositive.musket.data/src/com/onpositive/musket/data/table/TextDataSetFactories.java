package com.onpositive.musket.data.table;

import java.util.ArrayList;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.text.TextClassificationDataSet;

public class TextDataSetFactories {

	public static IDataSet create(ITabularDataSet t, IColumn textColumn,IQuestionAnswerer question_answerer) {
		ArrayList<IColumn>columns=new ArrayList<>(t.columns());
		columns.remove(textColumn);
		ArrayList<IColumn> findClassColumn = ImageDataSetFactories.findClassColumns(columns);
		
		if (findClassColumn.size()>0) {
			return new TextClassificationDataSet(t, textColumn, findClassColumn);
		}
		return null;
	}

}
