package com.onpositive.musket.data.columntypes;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Image")
public class ImageColumnType extends AbstractColumnType {

	public ImageColumnType() {
		super("", "", "");
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		boolean like = prj.getRepresenter().like(c);
		if (like) {
			return ColumnPreference.STRICT;
		}
		return ColumnPreference.NEVER;
	}

	@Override
	public String typeId(IColumn column) {
		return "image";
	}

}
