package com.onpositive.dside.ui.editors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DeprecatedControl extends Composite {

	public DeprecatedControl(Composite parent) {
		super(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(this);
		Label deprectedLabel = new Label(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(deprectedLabel);
		deprectedLabel.setText("This editor's content is deprecated and can't be shown. Please reopen it if necessary.");
	}
	

}
