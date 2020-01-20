package com.onpositive.dside.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class ObjectEditorInput implements IEditorInput,IPersistableElement{
	
	protected Object object;
	
	public Object getObject() {
		return object;
	}

	public ObjectEditorInput(Object obj) {
		this.object=obj;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		if (object != null) {
			return object.getClass().getSimpleName();
		}
		return "Deprecated editor";
	}

	@Override
	public IPersistableElement getPersistable() {
		return this;
	}

	@Override
	public String getToolTipText() {
		return null;
	}

	@Override
	public void saveState(IMemento memento) {
		
	}

	@Override
	public String getFactoryId() {
		return ObjectEditorInputFactory.ID;
	}

}
