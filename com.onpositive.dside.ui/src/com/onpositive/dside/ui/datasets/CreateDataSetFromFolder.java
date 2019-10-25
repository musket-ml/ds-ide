package com.onpositive.dside.ui.datasets;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.datasets.visualisation.ui.views.FolderEditorInput;
import com.onpositive.musket.data.images.FolderDataSet;

public class CreateDataSetFromFolder implements  IHandler{

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			IAdaptable r=(IAdaptable) ((IStructuredSelection) selection).getFirstElement();
		    IFolder adapter = r.getAdapter(IFolder.class);
			
			FolderEditorInput folderEditorInput = new FolderEditorInput(adapter);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(folderEditorInput, "com.onpositive.datasets.visualisation.ui.datasetEditor");
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
		
	}

}
