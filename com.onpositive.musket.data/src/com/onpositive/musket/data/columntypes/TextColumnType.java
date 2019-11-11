package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Text")
public class TextColumnType extends AbstractColumnType{

	public TextColumnType() {
		super("", "","");
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		if (c.caption().toLowerCase().contains("title")||c.caption().toLowerCase().contains("text")) {
			return ColumnPreference.STRICT;
		}
		if (isText(c)) {
			
			return ColumnPreference.STRICT;
		}
		return ColumnPreference.NEVER;
	}
	
	public static boolean isText(IColumn c) {
		return isText(c.uniqueValues());
	}
	

	public static boolean isText(Collection<Object> linkedHashSet) {
		int textCount = 0;
		int notTextCount=0;
		for (Object o : linkedHashSet) {
			try {
				if (o==null) {
					continue;
				}
				String string = o.toString();
				if (looksLikeText(string)) {
					textCount++;
				}
				else {
					notTextCount++;
				}
				if (notTextCount>2000&&textCount<200) {
					return false;
				}
				if (textCount>10000) {
					return true;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return textCount > linkedHashSet.size() / 2;
	}

	private static boolean looksLikeText(String string) {
		ArrayList<String> split = RLEMaskColumnType.fastSplitWS(string);
		int words = 0;
		if (split.size() > 2) {
			for (String m : split) {
				if (isWord(m)) {
					words++;
					if (words>3) {
						return true;
					}
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

	@Override
	public String typeId(IColumn column) {
		return "as_is";
	}

}
