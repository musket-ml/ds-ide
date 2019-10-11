package com.onpositive.musket.data.columntypes;

import java.util.Collection;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;

public class RLEMaskColumnType extends AbstractColumnType{

	public RLEMaskColumnType(String image, String id, String caption) {
		super(image, id, caption);
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		return null;
	}

	
	public static boolean like(IColumn c) {
		boolean isOk=true;
		Collection<Object> values = c.values();
		for (Object o:values) {
			if (o==null) {
				continue;
			}
			if (!o.toString().trim().equals("-1")&&!o.toString().trim().isEmpty()) {
				String[] split = o.toString().trim().split(" ");
				if (split.length%2!=0) {
					return false;
				}
				for (String s:split) {
					try {
					Integer.parseInt(s);
					}catch (Exception e) {
						return false;
					}
				}
			}
		}
		return isOk;
	}
}
