package com.onpositive.datasets.visualisation.ui.views;

import org.eclipse.swt.widgets.Display;

import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class BasicQuestionAnswerer implements IQuestionAnswerer{

	private final class MyRunnable implements Runnable {
		private final Object model;
		private boolean result;

		private MyRunnable(Object model) {
			this.model = model;
		}

		@Override
		public void run() {
			result=WidgetRegistry.createObject(model);
			
		}
	}

	@Override
	public boolean askQuestion(String title, Object model) {
		if (Display.getCurrent()==null) {
			MyRunnable runnable = new MyRunnable(model);
			Display.getDefault().syncExec(runnable);
			return runnable.result;
		}
		return WidgetRegistry.createObject(model);
	}

	

}
