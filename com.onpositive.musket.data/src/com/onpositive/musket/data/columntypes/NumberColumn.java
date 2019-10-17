package com.onpositive.musket.data.columntypes;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Number")
public class NumberColumn extends AbstractColumnType{

	public NumberColumn() {
		super("", "", "");
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		boolean hasDot=false;
		boolean hasMinus=false;
		for (Object o:c.uniqueValues()) {
			if (o !=null) {
				try {
					String string = o.toString();
					if (string.isEmpty()) {
						continue;
					}
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

	@Override
	public String typeId(IColumn column) {
		return "number";
	}

}
