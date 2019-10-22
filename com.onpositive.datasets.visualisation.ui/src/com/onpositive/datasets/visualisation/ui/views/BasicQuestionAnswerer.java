package com.onpositive.datasets.visualisation.ui.views;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class BasicQuestionAnswerer implements IQuestionAnswerer{

	private final class MyRunnable implements Runnable {
		private final Object model;
		private String title;
		private boolean result;

		private MyRunnable(String title,Object model) {
			this.model = model;
			this.title=title;
		}

		@Override
		public void run() {
			result=ask(title,model);
			
		}
	}

	@Override
	public boolean askQuestion(String title, Object model) {
		if (Display.getCurrent()==null) {
			MyRunnable runnable = new MyRunnable(title,model);
			Display.getDefault().syncExec(runnable);
			return runnable.result;
		}
		return ask(title, model);
	}

	protected boolean ask(String title,Object model) {
		if (model.equals(Boolean.TRUE)) {
			return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), title, title);
		}
		return WidgetRegistry.createObject(model);
	}

	

}
