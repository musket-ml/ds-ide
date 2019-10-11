package com.onpositive.musket.data.columntypes;

import java.util.Collection;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;

public class TextColumnType extends AbstractColumnType{

	public TextColumnType(String image, String id, String caption) {
		super(image, id, caption);
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		if (isText(c)) {
			return ColumnPreference.STRICT;
		}
		return ColumnPreference.NEVER;
	}
	
	public static boolean isText(IColumn c) {
		return isText(c.values());
	}

	public static boolean isText(Collection<Object> linkedHashSet) {
		int textCount = 0;
		for (Object o : linkedHashSet) {
			try {
				String string = o.toString();
				if (looksLikeText(string)) {
					textCount++;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return textCount > linkedHashSet.size() / 2;
	}

	private static boolean looksLikeText(String string) {
		String[] split = string.split(" ");
		int words = 0;
		if (split.length > 2) {
			for (String m : split) {
				if (isWord(m)) {
					words++;
				}
			}
		}
		return words > 3;
	}

	public static boolean isNumber(String m) {
		try {
			Double.parseDouble(m);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isWord(String m) {
		for (int i = 0; i < m.length(); i++) {
			char charAt = m.charAt(i);
			if (!Character.isLetter(charAt)) {
				return false;
			}
		}
		return true;
	}

}
