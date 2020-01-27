package com.onpositive.dside.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.onpositive.dside.ui.editors.preview.MusketPreviewEditorPart;

public class RefreshAugmentationsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MusketPreviewEditorPart previewEditor = getPreviewEditor();
		if (previewEditor != null) {
			previewEditor.updatePreview();
		}
		return null;
	}
	
	protected MusketPreviewEditorPart getPreviewEditor() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench==null){
			return null;
		}
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow==null){
			return null;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage==null){
			return null;
		}
		IEditorPart editor = activePage.getActiveEditor();
		
		if (editor instanceof MusketPreviewEditorPart){
			return (MusketPreviewEditorPart) editor;
		}
		return null;
	}

}
