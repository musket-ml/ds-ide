package com.onpositive.datasets.visualisation.ui.views;

import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class BasicQuestionAnswerer implements IQuestionAnswerer{

	@Override
	public boolean askQuestion(String title, Object model) {
		return WidgetRegistry.createObject(model);
	}

	

}
