package com.onpositive.dside.ui.editors;


import com.onpositive.commons.SWTImageManager;
import com.onpositive.dside.wizards.MusketProjectNameAndLocationWizardPage;

public class WebServiceProjectNameAndLoacation extends MusketProjectNameAndLocationWizardPage{

	public WebServiceProjectNameAndLoacation(String pageName) {
		super(pageName);
		setTitle("Rest API Project");
        setDescription("Create a new REST Api from Musket experiment");
        setPageComplete(false);
        setImageDescriptor(SWTImageManager.getDescriptor("web_prj_wiz"));
	}

}
