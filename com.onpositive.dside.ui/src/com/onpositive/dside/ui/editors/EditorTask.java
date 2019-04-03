package com.onpositive.dside.ui.editors;

import com.onpositive.musket_core.Experiment;

public abstract class EditorTask {

	protected String image;
	protected String name;
	
	public EditorTask(String name,String image) {
		this.name=name;
		this.image=image;
	}
	
	public abstract void perform(ExperimentOverivewEditorPart editor,Experiment exp)  ;
}
