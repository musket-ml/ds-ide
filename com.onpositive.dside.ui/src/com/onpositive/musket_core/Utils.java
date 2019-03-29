package com.onpositive.musket_core;

import java.util.ArrayList;
import java.util.function.Function;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.dside.dto.PythonError.StackElement;

public class Utils {

	public static<T,A> ArrayList<A> asList(IList<T>t,Function<T,A>c){
		ArrayList<A>r=new ArrayList<>();
		int size = t.size();
		for (int i=0;i<size;i++) {
			r.add(c.apply(t.get(i)));
		}
		return r;
	}
	
	public static void openWithDefault(StackElement stackElement, String file,int line) {
		IFile[] findFilesForLocation = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(file));
		if (findFilesForLocation.length>0) {
			FileEditorInput fileEditorInput = new FileEditorInput(findFilesForLocation[0]);
			IEditorDescriptor defaultEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(findFilesForLocation[0].getName());
			try {
				IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(fileEditorInput,defaultEditor.getId());
				if (openEditor instanceof TextEditor) {
					TextEditor tr=(TextEditor) openEditor;
					int start=tr.getDocumentProvider().getDocument(tr.getEditorInput()).getLineOffset(line>0?line-1:0);
					tr.selectAndReveal(start, 0);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}
