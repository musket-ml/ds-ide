package com.onpositive.dside.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.dside.ui.IMusketConstants;

public class DSIDEResourceLinkHelper implements ILinkHelper {

	@Override
	public IStructuredSelection findSelection(IEditorInput anInput) {		
		IFile file = ResourceUtil.getFile(anInput);
		if (file != null) {
			if (file.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
				ExperimentNode experimentNode = new ExperimentNode((IFolder) file.getParent());
//				return new StructuredSelection(file.getParent());	
				return new StructuredSelection(experimentNode);	
			}
			return new StructuredSelection(file);
		}
		return StructuredSelection.EMPTY;
	}

	@Override
	public void activateEditor(IWorkbenchPage aPage,
			IStructuredSelection aSelection) {
		if (aSelection == null || aSelection.isEmpty())
			return;
		if (aSelection.getFirstElement() instanceof IFile) {
			IEditorInput fileInput = new FileEditorInput((IFile) aSelection.getFirstElement());
			IEditorPart editor = null;
			if ((editor = aPage.findEditor(fileInput)) != null)
				aPage.bringToTop(editor);
		}

	}
}
