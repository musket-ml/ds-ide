package com.onpositive.dside.ui.datasets;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.datasets.visualisation.ui.views.FolderEditorInput;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.FolderDataSet;

public class CreateDataSetFromFolder implements IHandler {

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
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
		if (selection instanceof IStructuredSelection) {
			IAdaptable r = (IAdaptable) ((IStructuredSelection) selection).getFirstElement();
			IFolder adapter = r.getAdapter(IFolder.class);
			IDataSet createDataSetFromFolder;
			try {
				createDataSetFromFolder = FolderDataSet.createDataSetFromFolder(adapter.getLocation().toFile(),
						adapter.getDefaultCharset());

				if (createDataSetFromFolder == null) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Can not create dataset",
							"Sorry at this moment we only support folders that contain images or conll text files, or folder that contain folders containing image files");
					return null;
				}

				FolderEditorInput folderEditorInput = new FolderEditorInput(adapter);
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(folderEditorInput,
							"com.onpositive.datasets.visualisation.ui.datasetEditor");
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
