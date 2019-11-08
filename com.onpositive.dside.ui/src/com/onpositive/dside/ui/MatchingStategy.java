package com.onpositive.dside.ui;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

public class MatchingStategy implements IEditorMatchingStrategy {

	@Override
	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		if (input instanceof FileEditorInput)
		{
			FileEditorInput fe=(FileEditorInput) input;
			if (fe.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
				IEditorInput editorInput;
				try {
					editorInput = editorRef.getEditorInput();
					if (editorInput instanceof FileEditorInput) {
						if (((FileEditorInput) editorInput).getFile().equals(fe.getFile())) {
							return true;
						}
					}
					return false;
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return false;
	}

}
