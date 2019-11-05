package com.onpositive.dside.ui.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;

import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.musket_core.IServer;

public class DownloadDepsAction implements IObjectActionDelegate {
	public DownloadDepsAction() {
		
	}

	@Override
	public void run(IAction action) {
		if(action instanceof ObjectPluginAction) {
			ObjectPluginAction pluginAction = (ObjectPluginAction)action;
			
			ISelection selection = pluginAction.getSelection();
			
			if(selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelecetion = (IStructuredSelection)selection;
				
				Object firstElement = structuredSelecetion.getFirstElement();
				
				if(firstElement instanceof IProject) {
					IProject project = (IProject)firstElement;
					
					download(project);
				}
			}
		}
	}
	
	private void download(IProject project) {
		String fullPath = project.getLocation().toOSString();
			
		GateWayRelatedTask serverTask = new GateWayRelatedTask(project, new IGateWayServerTaskDelegate() {
			@Override
			public void terminated() {
								
			}
			
			@Override
			public void started(GateWayRelatedTask task) {
				
			}
		});
				
		serverTask.getServer().thenAcceptAsync((IServer server) -> {
			try {
				server.downloadDeps(fullPath);
				
			} catch(Throwable t) {
				t.printStackTrace();
			}
			
			serverTask.terminate();
		});
		
		TaskManager.perform(serverTask);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}
}