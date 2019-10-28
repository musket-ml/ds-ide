package com.onpositive.musket.data.table;

import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.labels.LabelsSet;

public interface IHasLabels {

	List<String> classNames();

	void setLabels(LabelsSet labelsSet);

	Map<String, Object> getSettings();

	LabelsSet labels();

}
