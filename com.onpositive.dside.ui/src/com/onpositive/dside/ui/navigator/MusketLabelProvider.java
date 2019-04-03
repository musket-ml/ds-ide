package com.onpositive.dside.ui.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.onpositive.commons.SWTImageManager;

/**
 * Provides the labels for the pydev package explorer.
 *
 * @author Fabio
 */
public class MusketLabelProvider implements ILabelProvider {

    private WorkbenchLabelProvider provider;

    private volatile Image projectWithError = null;

    private Object lock = new Object();

    public MusketLabelProvider() {
        provider = new WorkbenchLabelProvider();
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
       if (element instanceof ExperimentsNode) {
    	   return SWTImageManager.getImage("experiments_obj");
       }
       if (element instanceof ExperimentNode) {
    	   return SWTImageManager.getImage("experiment");
       }
       if (element instanceof ExperimentGroup) {
    	   return SWTImageManager.getImage("experiment_group");
       }
       return provider.getImage(element);
    }


    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof ExperimentsNode) {
            return "experiments";
        }
        if (element instanceof ExperimentNode) {
        	ExperimentNode n=(ExperimentNode) element;
            return n.folder.getName();
        }
        if (element instanceof ExperimentGroup) {
        	ExperimentGroup n=(ExperimentGroup) element;
            String portableString = n.getPath().toPortableString();
            if (portableString.isEmpty()) {
            	return "default";
            }
			return portableString;
        }
        return provider.getText(element);
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
    public void addListener(ILabelProviderListener listener) {
        provider.addListener(listener);
    }

    @Override
    public void dispose() {
        provider.dispose();
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return provider.isLabelProperty(element, property);
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        provider.removeListener(listener);
    }

}
