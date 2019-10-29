package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.Column;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.musket.data.table.SubColumn;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Class")
public class ClassColumnType extends AbstractColumnType implements ISmartColumnType {

	public ClassColumnType() {
		super("", "", "");
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		return check(c);
	}

	@Override
	public SmartColumnPref is2(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		ColumnPreference check = check(c);
		if (check == ColumnPreference.STRICT) {
			ArrayList<Object> uniqueValues = c.uniqueValues();
			if (uniqueValues.size() > 500) {
				boolean hasSplits = false;
				for (Object o : uniqueValues) {
					String string = o.toString();
					if (string.contains("_")) {
						hasSplits = true;
						break;
					}
					if (string.contains("|")) {
						hasSplits = true;
						break;
					}
					if (string.contains(" ")) {
						hasSplits = true;
						break;
					}
				}
				if (hasSplits) {
					// this seems to be a multi class classification, let's check it for validity
					boolean consistentLength = true;
					boolean hasDubplicateClassValues = false;
					int ln = -1;
					for (Object o : uniqueValues) {
						String string = o.toString();
						String[] split = new String[] { string };
						if (string.contains("_")) {
							split = string.split("_");

						} else if (string.contains("|")) {
							split = string.split("|");

						} else if (string.contains(" ")) {
							split = string.split(" ");

						}
						ArrayList<String> splitted = new ArrayList<>();

						for (String s : split) {
							splitted.add(s.trim());
						}
						if (ln == -1) {
							ln = splitted.size();
						}
						if (new LinkedHashSet<>(splitted).size() != splitted.size()) {
							hasDubplicateClassValues = true;
						}
						if (splitted.size() != ln) {
							consistentLength = false;
						}
					}
					if (hasDubplicateClassValues) {
						if (answerer.askQuestion("Column " + c.id()
								+ " seems to contain several independent classses, should we split it on subcolumns?",
								true)) {
							ArrayList<SubColumn> cls = new ArrayList<>();
							cls.add(new SubColumn(c.id(), c.caption() + ":0", (Column) c, 0));
							cls.add(new SubColumn(c.id(), c.caption() + ":1", (Column) c, 1,-1));
							return new SmartColumnPref(check, cls);
						}

					}
				}
			}
		}
		return new SmartColumnPref(check, null);
	}

	public static ColumnPreference check(IColumn c) {
		if (c.id().equals("publication_id")) {
			System.out.println("A");
		}
		String lowerCase = c.caption().toLowerCase();
		if (lowerCase.contains("class") || lowerCase.contains("clazz") || lowerCase.contains("classes")) {
			return ColumnPreference.STRICT;
		}
		if (lowerCase.contains("attribute")) {
			return ColumnPreference.STRICT;
		}
		ArrayList<Object> uniqueValues = c.uniqueValues();
		if (uniqueValues.size() < 1000) {
			if (looksDouble(uniqueValues)) {
				return ColumnPreference.NEVER;
			}
			if (isBool(uniqueValues)) {
				return ColumnPreference.STRICT;
			}
			if (isAllInt(uniqueValues) || isAllString(uniqueValues)) {
				return ColumnPreference.MAYBE;
			}
		}
		return ColumnPreference.NEVER;
	}

	private static boolean isBool(Collection<Object> linkedHashSet) {
		if (linkedHashSet.size() == 2) {
			return true;
		}
		return false;
	}
	private static boolean looksDouble(Collection<Object> linkedHashSet) {
		int ck=0;
		for (Object o:linkedHashSet) {
			if (o==null||o.toString().isEmpty()) {
				continue;
			}
			try {
				String sm=o.toString();
				if (sm.indexOf('.')!=-1) {
					double vl=Double.parseDouble(sm);
					ck++;
					if (ck>10) {
						return true;
					}
				}
			}catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	private static boolean isAllString(Collection<Object> linkedHashSet) {
		for (Object s0 : linkedHashSet) {
			if (s0 == null) {
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

	private static boolean isAllInt(Collection<Object> linkedHashSet) {
		for (Object o : linkedHashSet) {
			try {
				if (o == null) {
					continue;
				}
				Integer.parseInt(o.toString());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String typeId(IColumn column) {
		ArrayList<Object> uniqueValues = column.uniqueValues();
		if (uniqueValues.size() == 2) {
			return "binary";
		}
		return "one_hot";
	}

}