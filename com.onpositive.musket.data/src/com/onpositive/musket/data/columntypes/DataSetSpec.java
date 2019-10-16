package com.onpositive.musket.data.columntypes;

import java.util.List;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ImageRepresenter;

public class DataSetSpec {
	public final ColumnLayout layout;
	public final ITabularDataSet tb;
	public final DataProject prj;
	public final IQuestionAnswerer answerer;
	public final ImageRepresenter representer;

	public DataSetSpec(ColumnLayout layout, ITabularDataSet tb, DataProject prj, IQuestionAnswerer answerer) {
		this.layout = layout;
		this.tb = tb;
		this.prj = prj;
		this.answerer = answerer;
		this.representer=prj.getRepresenter();
	}
	public DataSetSpec(ITabularDataSet tb, ImageRepresenter rep) {
		this.layout = null;
		this.tb = tb;
		this.prj = null;
		this.answerer = null;
		this.representer=rep;
	}
	
	public IColumn getStrictColumn(Class<? extends IColumnType>clazz) {
		for (IColumn c:layout.infos.keySet()) {
			if (layout.infos.get(c).preferredType()==clazz) {
				return c;
			}
		}
		return null;		
	}

	public List<? extends IColumn> columns() {
		return tb.columns();
	}

	public ImageRepresenter getRepresenter() {
		return representer;
	}

	public ITabularDataSet base() {
		return tb;
	}
}