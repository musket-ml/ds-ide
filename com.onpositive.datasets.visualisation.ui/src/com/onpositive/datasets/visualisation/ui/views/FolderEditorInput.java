package com.onpositive.datasets.visualisation.ui.views;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Adapter for making a file resource a suitable input for an editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class FolderEditorInput extends PlatformObject implements IURIEditorInput,
		IPersistableElement {
	private IFolder file;

	/**
	 * Return whether or not file is local. Only {@link IFile}s with a local
	 * value should call {@link IPathEditorInput#getPath()}
	 * @param file
	 * @return boolean <code>true</code> if the file has a local implementation.
	 * @since 3.4
	 */
	public static boolean isLocalFile(IFile file){

		IPath location = file.getLocation();
		if (location != null)
			return true;
		//this is not a local file, so try to obtain a local file
		return false;

	}

	/**
	 * Creates an editor input based of the given file resource.
	 *
	 * @param file the file resource
	 */
	public FolderEditorInput(IFolder file) {
		if (file == null)
			throw new IllegalArgumentException();
		this.file = file;

	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	/*
	 * The <code>FileEditorInput</code> implementation of this
	 * <code>Object</code> method bases the equality of two
	 * <code>FileEditorInput</code> objects on the equality of their underlying
	 * <code>IFile</code> resources.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof IFileEditorInput)) {
			return false;
		}
		IFileEditorInput other = (IFileEditorInput) obj;
		return file.equals(other.getFile());
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public String getFactoryId() {
		return FolderEditorInputFactory.getFactoryId();
	}

	public IFolder getFolder() {
		return file;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return this;
	}

	

	@Override
	public String getToolTipText() {
		return file.getFullPath().makeRelative().toString();
	}

	@Override
	public void saveState(IMemento memento) {
		FolderEditorInputFactory.saveState(memento, this);
	}



	@Override
	public URI getURI() {
		return file.getLocationURI();
	}


	
	public IPath getPath() {
		IPath location = file.getLocation();
		if (location != null)
			return location;
		return null;
	}


	@Override
	public String toString() {
		return getClass().getName() + "(" + file.getFullPath() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * Allows for the return of an {@link IWorkbenchAdapter} adapter.
	 *
	 * @since 3.5
	 *
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@Override
	public <T> T getAdapter(Class<T> adapterType) {
		if (IWorkbenchAdapter.class.equals(adapterType)) {
			return adapterType.cast(new IWorkbenchAdapter() {

				@Override
				public Object[] getChildren(Object o) {
					return new Object[0];
				}

				@Override
				public ImageDescriptor getImageDescriptor(Object object) {
					return FolderEditorInput.this.getImageDescriptor();
				}

				@Override
				public String getLabel(Object o) {
					return FolderEditorInput.this.getName();
				}

				@Override
				public Object getParent(Object o) {
					return FolderEditorInput.this.file.getParent();
				}
			});
		}

		return super.getAdapter(adapterType);
	}

	
}
