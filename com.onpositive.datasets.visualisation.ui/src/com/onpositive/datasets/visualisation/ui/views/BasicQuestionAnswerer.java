package com.onpositive.datasets.visualisation.ui.views;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListSelectionDialog;

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

	@SuppressWarnings("unchecked")
	protected boolean ask(String title,Object model) {
		if (model.equals(Boolean.TRUE)) {
			return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), title, title);
		}
		if (model.equals(Boolean.FALSE)) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), title, title);
			return false;
		}
		if (model instanceof Collection) {
			ListSelectionDialog sl=new ListSelectionDialog(Display.getCurrent().getActiveShell(), ((Collection) model).toArray(), new ArrayContentProvider(), 
					new LabelProvider() {
				@Override
				public String getText(Object element) {
					return element.toString();
				}
			}, title);
			int open = sl.open();
			if (open!=Dialog.OK) {
				return false;
			}
			
			@SuppressWarnings("rawtypes")
			Collection list = (Collection) model;
			list.clear();
			list.addAll(Arrays.asList(sl.getResult()));
			return true;
		}
		return WidgetRegistry.createObject(model);
	}

	

}
