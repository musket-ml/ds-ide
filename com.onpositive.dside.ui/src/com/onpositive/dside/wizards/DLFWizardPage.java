package com.onpositive.dside.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.elements.RootElement;

public abstract class DLFWizardPage extends WizardPage{

	public DLFWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public DLFWizardPage(String pageName) {
		super(pageName);
	}

	protected RootElement el;

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		recustively(el.getContentParent(), visible);
		
	}
	
	void recustively(Control c,boolean visible) {
		if (c instanceof Composite) {
			Composite cm=(Composite) c;
			for (Control ca:cm.getChildren()) {
				recustively(ca, visible);
			}
		}
		c.setVisible(visible);
	}
}
