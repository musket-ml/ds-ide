package com.onpositive.dside.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.python.pydev.ui.perspective.PythonPerspectiveFactory;

import com.onpositive.dside.ui.views.ExperimentsView;

public class DSIDEPerspectiveFactory extends PythonPerspectiveFactory {
	
	public static final String PERSPECTIVE_ID = "com.onpositive.dside.ui.perspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		super.createInitialLayout(layout);
		layout.addShowViewShortcut(ExperimentsView.ID);
	}

}
