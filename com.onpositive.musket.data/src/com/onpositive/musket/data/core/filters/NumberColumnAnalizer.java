package com.onpositive.musket.data.core.filters;

import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.generic.GenericItem;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularItem;

public class NumberColumnAnalizer extends AbstractAnalizer implements IColumnDependentAnalizer,IAnalizer<INoneDataSet>{

	protected IColumn column;
	
	public NumberColumnAnalizer(IColumn column) {
		super();
		this.column = column;
	}

	@Override
	public String getName(IColumn c) {
		return "Group by "+c.caption();
	}

	@Override
	protected Object group(IItem v) {
		GenericItem gr=(GenericItem) v;
		ITabularItem base = gr.base();
		try {
			return Double.parseDouble(column.getValueAsString(base));
		}catch (NumberFormatException e) {
			return "None";
		}
	}

}
