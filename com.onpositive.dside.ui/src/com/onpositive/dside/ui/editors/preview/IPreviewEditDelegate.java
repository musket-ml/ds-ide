package com.onpositive.dside.ui.editors.preview;

import java.util.function.Consumer;

import com.onpositive.dside.tasks.analize.IAnalizeResults;

public interface IPreviewEditDelegate {
	
	int getOffset();
	
	String getInitialText();
	
	void setText(String text);
	
	String getText();
	
	void refreshPreview(Consumer<IAnalizeResults> onSuccess, Consumer<Throwable> onFail);
	
	/**
	 * Save modified text back, e.g. to original editor
	 */
	void save();
	
}
