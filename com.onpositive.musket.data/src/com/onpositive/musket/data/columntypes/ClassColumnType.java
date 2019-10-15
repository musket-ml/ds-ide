package com.onpositive.musket.data.columntypes;

import java.util.LinkedHashSet;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;

public class ClassColumnType extends AbstractColumnType{

	public ClassColumnType(String image, String id, String caption) {
		super(image, id, caption);
	}
	
	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		return check(c);
	}

	public static ColumnPreference check(IColumn c) {
		String lowerCase = c.caption().toLowerCase();
		if (lowerCase.contains("class") || lowerCase.contains("clazz")
				|| lowerCase.contains("classes")) {
			return ColumnPreference.STRICT;
		}
		LinkedHashSet<Object> linkedHashSet = new LinkedHashSet<>(c.values());
		if (linkedHashSet.size() < 1000) {
			if (isBool(linkedHashSet)){
				return ColumnPreference.STRICT;
			}
			if (isAllInt(linkedHashSet) || isAllString(linkedHashSet) ) {
				return ColumnPreference.MAYBE;
			}
		}
		return ColumnPreference.NEVER;
	}
	
	private static boolean isBool(LinkedHashSet<Object> linkedHashSet) {
		if (linkedHashSet.size() == 2) {
			return true;
		}
		return false;
	}

	private static boolean isAllString(LinkedHashSet<Object> linkedHashSet) {
		for (Object s0 : linkedHashSet) {
			if (s0==null) {
				continue;
			}
			String s = s0.toString();
			
			if (s.isEmpty()) {
				return true;
			}
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				return false;
			}
			for (int i = 0; i < s.length(); i++) {
				if (!Character.isJavaIdentifierPart(s.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
	

	private static boolean isAllInt(LinkedHashSet<Object> linkedHashSet) {
		for (Object o : linkedHashSet) {
			try {
				if( o==null) {
					continue;
				}
				Integer.parseInt(o.toString());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

}