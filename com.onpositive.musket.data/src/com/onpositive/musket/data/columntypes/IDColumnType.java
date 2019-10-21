package com.onpositive.musket.data.columntypes;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Id")
public class IDColumnType extends AbstractColumnType{

	public IDColumnType() {
		super("", "", "");
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		if (c.values().size()==c.uniqueValues().size()) {
			return ColumnPreference.STRICT;
		}
		return ColumnPreference.NEVER;
	}

	@Override
	public String typeId(IColumn column) {
		return "as_is";
	}

}
