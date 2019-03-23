package com.onpositive.dside.ui.editors.yaml.model;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;

public class MetaFactory {

	public static BaseMeta stringProperty(String name, boolean multiValue) {
		BaseMeta m = new BaseMeta();
		if (multiValue) {
			m.putMeta(DefaultMetaKeys.MULTI_VALUE_KEY, multiValue);
		}
		m.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, String.class);
		return m;
	}
}
