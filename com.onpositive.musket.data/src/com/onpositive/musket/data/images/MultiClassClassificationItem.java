package com.onpositive.musket.data.images;

import java.util.ArrayList;

import com.onpositive.musket.data.table.ITabularItem;

public class MultiClassClassificationItem extends BinaryClassificationItem implements IMulticlassClassificationItem {

	public MultiClassClassificationItem(BinaryClassificationDataSet binarySegmentationDataSet, ITabularItem v) {
		super(binarySegmentationDataSet, v);
	}

	protected boolean isPositiveValue(Object value) {
		String valueAsString = value.toString();
		if (valueAsString.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<String> classes() {
		String value = (String) this.base.clazzColumn.getValue(item);
		return splitByClass(value);
	}

	protected static ArrayList<String> splitByClass(String value) {
		ArrayList<String> classes = new ArrayList<>();
		if (value.indexOf(' ') != -1) {
			String[] split = value.split(" ");
			for (String s : split) {
				classes.add(s.trim());
			}
		} else if (value.indexOf('|') != -1) {

			String[] split = value.split("|");
			for (String s : split) {
				classes.add(s.trim());
			}
		} else {
			if (value.trim().isEmpty()) {

				classes.add("Empty");

			} else {
				classes.add(value.trim());
			}
		}
		return classes;
	}
}
