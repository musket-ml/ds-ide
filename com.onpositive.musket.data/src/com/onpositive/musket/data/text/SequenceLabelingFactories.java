package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.onpositive.musket.data.columntypes.BasicColumn;
import com.onpositive.musket.data.columntypes.ClassColumnType;
import com.onpositive.musket.data.columntypes.ColumnLayout;
import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.musket.data.columntypes.NumberColumn;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.IColumn;

public class SequenceLabelingFactories implements IDataSetFactory{

	@Override
	public String caption() {
		return "Sequence Labeling";
	}
	
	static class SequenceLayout{
		protected IColumn sentenceColumn;
		protected IColumn docColumn;
		protected IColumn wordColumn;
		protected ArrayList<IColumn>classColumns;
		
		public SequenceLayout(IColumn sentenceColumn, IColumn docColumn, IColumn wordColumn,
				ArrayList<IColumn> classColumns) {
			super();
			this.sentenceColumn = sentenceColumn;
			this.docColumn = docColumn;
			this.wordColumn = wordColumn;
			this.classColumns = classColumns;
		}
		
	}

	@Override
	public double estimate(DataSetSpec parameterObject) {
		ColumnLayout layout = parameterObject.layout;
		Collection<ColumnInfo> infos = layout.infos();		
		ArrayList<IColumn>classificationColumns=new ArrayList<>();
		
		ColumnInfo bi=null;
		IColumn sec=null;
		IColumn doc=null;
		for (ColumnInfo i:infos) {
			if (i.getColumn().id().equals("sentence_id")) {
				sec=i.getColumn();
				continue;
			}
			if (i.getColumn().id().equals("doc_id")) {
				doc=i.getColumn();
				continue;
			}			
			if (i.preferredType()==ClassColumnType.class) {
				classificationColumns.add(i.getColumn());
			}
			if (i.preferredType()==BasicColumn.class) {
				if(bi==null) {
					bi=i;
				}
				else {
					bi=null;
					return 0;
				}				
			}
			
		}
		if (bi!=null&&!classificationColumns.isEmpty()&&(sec!=null||doc!=null)){
			parameterObject.setExtension(new SequenceLayout(sec, doc, bi.getColumn(), classificationColumns));
			return 1;
		}
		return 0;
	}

	@Override
	public IDataSet create(DataSetSpec spec, Map<String, Object> options) {
		if (options!=null) {
			return new TextSequenceDataSet(spec,options);
		}
		SequenceLayout extension = (SequenceLayout) spec.getExtension();
		return new TextSequenceDataSet(spec,extension);		
	}

}
