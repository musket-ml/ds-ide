package com.onpositive.dside.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;

import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.musket_core.IServer;

public class DownloadDepsAction extends MusketProjectAction {

	@Override
	public void run(IAction action) {
		IProject musketProject = getFirstSelectedMusketProject(selection);
		if (musketProject != null) {
			download(musketProject);			
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
	
	
}