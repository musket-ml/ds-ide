package com.onpositive.dside.ui.builder;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.onpositive.musket_core.ProjectManager;

public class SampleBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {

		HashSet<IProject> projects = new HashSet<>();

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource instanceof IProject) {
				return true;
			}
			if (resource instanceof IFolder) {
				if (resource.getName().equals("modules")) {
					return true;
				}
			}
			if (resource.getName().endsWith(".py")) {

				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					// handle added resource
					projects.add(resource.getProject());
					break;
				case IResourceDelta.REMOVED:
					projects.add(resource.getProject());
					// handle removed resource
					break;
				case IResourceDelta.CHANGED:
					projects.add(resource.getProject());

					break;
				}
			}
			// return true to continue visiting children.
			return false;
		}
	}

	public static final String BUILDER_ID = "com.onpositive.dside.ui.musketBuilder";

	public static final String MARKER_TYPE = "de.jcup.yamleditor.script.problem";

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		ProjectManager.getInstance(project).refresh(null);
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		SampleDeltaVisitor visitor = new SampleDeltaVisitor();
		delta.accept(visitor);
		visitor.projects.forEach(v -> {
			try {
				ProjectManager.getInstance(v).refresh(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}
}
