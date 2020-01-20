package com.onpositive.musket_core;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import com.onpositive.dside.ui.IMusketConstants;

public class ExperimentFinder {

	
	public static Collection<Experiment>find(List<IContainer>fld){
		LinkedHashSet<Experiment>experiments=new LinkedHashSet<>();
		fld.forEach(f->{
			try {
				f.accept(new IResourceVisitor() {
					
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource instanceof IFolder) {
							Experiment experiment = getExperiment(resource);
							if (experiment != null) {
								experiments.add(experiment);
							}
						}
						
						return true;
					}
				});
			} catch (CoreException e) {
				e.printStackTrace();
			}
		});
		return experiments;
	}
	
	public static Experiment getExperiment(IResource resource) {
		if (resource instanceof IFolder) {
			IFile file = ((IFolder) resource).getFile(IMusketConstants.MUSKET_CONFIG_FILE_NAME);
			if (file.exists()) {
				return new Experiment(resource.getLocation().toPortableString());
			}
		} else if (resource instanceof IFile && resource.getName().equals(IMusketConstants.MUSKET_CONFIG_FILE_NAME)) {
			return new Experiment(resource.getParent().getLocation().toPortableString());
		}
		
		return null;
	}
}
