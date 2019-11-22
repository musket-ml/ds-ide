package com.onpositive.dside.ui;

import org.eclipse.swt.widgets.Composite;

import com.onpositive.dside.tasks.analize.IAnalizeResults;

public class StandaloneDataSetGallery extends DataSetGallery {
	
	private static final long serialVersionUID = 7856339056611819269L;

	public StandaloneDataSetGallery(Composite parent, IAnalizeResults input) {
		setInput(input);
		createControl(parent);
	}

}
