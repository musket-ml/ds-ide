package com.onpositive.musket.data.columntypes;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;

public class IDColumnType extends AbstractColumnType{

	public IDColumnType(String image, String id, String caption) {
		super(image, id, caption);
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		if (c.values().size()==c.uniqueValues().size()) {
			return ColumnPreference.STRICT;
		}
		return ColumnPreference.NEVER;
	}

}
