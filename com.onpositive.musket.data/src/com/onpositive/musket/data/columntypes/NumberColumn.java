package com.onpositive.musket.data.columntypes;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;

public class NumberColumn extends AbstractColumnType{

	public NumberColumn(String image, String id, String caption) {
		super(image, id, caption);
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		boolean hasDot=false;
		boolean hasMinus=false;
		for (Object o:c.uniqueValues()) {
			if (o !=null) {
				try {
					String string = o.toString();
					if (string.indexOf('.')!=-1) {
						hasDot=true;
					}
					if (string.indexOf('-')!=-1) {
						hasMinus=true;
					}
					Double.parseDouble(string);
				}
				catch (NumberFormatException e) {
					return ColumnPreference.NEVER;
				}
			}
		}
		if (hasDot||hasMinus) {
			return ColumnPreference.STRICT;
		}
		return ColumnPreference.MAYBE;
	}

}
