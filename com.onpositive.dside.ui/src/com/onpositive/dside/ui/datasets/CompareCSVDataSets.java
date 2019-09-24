package com.onpositive.dside.ui.datasets;

import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditorInput;

public class CompareCSVDataSets implements IHandler {

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
		Map parameters = event.getParameters();
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sm = (IStructuredSelection) selection;
			List list = sm.toList();
			IFile fl1 = (IFile) ((IAdaptable)list.get(0)).getAdapter(IFile.class);
			IFile fl2 = (IFile)  ((IAdaptable)list.get(1)).getAdapter(IFile.class);
			open(fl1, fl2);
		}
		return null;
	}

	public static void open(IFile fl1, IFile fl2) {
		IEditorInput[] inputs = new IEditorInput[] { new FileEditorInput(fl1), new FileEditorInput(fl2) };
		MultiEditorInput mi = new MultiEditorInput(
				new String[] { "com.onpositive.datasets.visualisation.ui.datasetEditor",
						"com.onpositive.datasets.visualisation.ui.datasetEditor" },
				inputs);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(mi,
					"com.onpositive.datasets.visualisation.ui.datasetEditor", true);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
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

	}

}