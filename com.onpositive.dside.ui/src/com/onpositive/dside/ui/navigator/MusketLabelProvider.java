package com.onpositive.dside.ui.navigator;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.dside.ui.IMusketConstants;

/**
 * Provides the labels for the pydev package explorer.
 *
 * @author Fabio
 */
public class MusketLabelProvider extends LabelProvider implements ILabelProvider {

    private WorkbenchLabelProvider provider;

    private volatile Image projectWithError = null;

    private Object lock = new Object();

    public MusketLabelProvider() {
        provider = new WorkbenchLabelProvider();
    }
     

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    
    protected HashSet<IFile>ff=new HashSet<>();
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
       if (element instanceof IFile) {
    	   IFile f=(IFile) element;
    	   if (f.getName().endsWith(".csv")||f.getName().endsWith(".tsv")) {
    		   try {
				String persistentProperty = f.getPersistentProperty(IDE.EDITOR_KEY);
				
				if (persistentProperty!=null&&persistentProperty.equals("com.onpositive.datasets.visualisation.ui.datasetEditor")) {
						Image image = SWTImageManager.getImage("com.onpositive.dside.ui.dataset");
						if (ff.add(f)) {
							fireLabelProviderChanged(new LabelProviderChangedEvent(this, f));
						}
						return image;
				   }
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	   }
       }
       return provider.getImage(element);
    }


    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof ExperimentsNode) {
            return IMusketConstants.MUSKET_EXPERIMENTS_FOLDER;
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
        super.addListener(listener);
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
        super.removeListener(listener);
    }

}
