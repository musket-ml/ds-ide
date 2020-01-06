package com.onpositive.musket_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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
import com.onpositive.semantic.model.api.property.Predicate;

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
	
	public static void copyDir(File source,File target) {
		copyDirInner(source, target,new Predicate<File>() {

			@Override
			public boolean apply(File arg0) {
				return true;
			}
		});
	}

	public static void copyDirInner(File source, File target,Predicate<File>needToCopy) {
		if (!needToCopy.apply(source)) {
			return ;
		}
		if (source.isDirectory()) {
			File[] listFiles = source.listFiles();
			if (!target.exists()) {
				target.mkdirs();
			}
			for (File f:listFiles) {
				copyDirInner(f, new File(target,f.getName()),needToCopy);
			}
		}
		else {			
			FileOutputStream fileOutputStream;
			try {
				fileOutputStream = new FileOutputStream(target);
				try {
					Files.copy(source.toPath(), fileOutputStream);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					finally {
						try {
							fileOutputStream.close();
						} catch (IOException e) {
							throw new RuntimeException(e);	
						}
					}
			} catch (FileNotFoundException e1) {
				throw new IllegalStateException(e1);
			}
			
		}
	}
	
}
