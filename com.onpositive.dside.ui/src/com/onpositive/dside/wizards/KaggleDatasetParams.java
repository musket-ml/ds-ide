package com.onpositive.dside.wizards;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.ResourcesPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.ui.DatasetTableElement;
import com.onpositive.musket_core.IServer;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/kaggleDatasetView.dlf")
public class KaggleDatasetParams {
	@Caption("Project")
	@Required
	String project;
		
	@Caption("Search")
	String dsSearch;
	
	@Caption("Use Dataset")
	Boolean dsEnabled = false;
	
	@Caption("My Datasets")
	Boolean dsIsMine = false;
	
	@Caption("Datasets")
	List<DatasetTableElement> dsSearchResultDatasets = new ArrayList<DatasetTableElement>();
	
	@Caption("Competitions")
	List<DatasetTableElement> dsSearchResultCompetitions = new ArrayList<DatasetTableElement>();
		
	public DatasetTableElement dsDatsetItem = null;
	public DatasetTableElement dsCompetitionItem = null;
	
	boolean dsWaiting = false;
	boolean dsNotWaiting = true;
	
	private void setWaiting(Boolean value) {
		dsWaiting = value;
		dsNotWaiting = !value;
	}
	
	boolean dsIsDataset = true;
	boolean dsIsCompetition = false;
	
	public boolean isDsDatasetEnabled() {
		return dsEnabled && dsIsDataset;
	}
	
	public boolean isDsCompetitionEnabled() {
		return dsEnabled && dsIsCompetition;
	}
	
	public DatasetTableElement getItem() {
		return isDsDatasetEnabled() ? dsDatsetItem : dsCompetitionItem;
	}
		
	@Caption("Search")
	public void searchButton() {
		setWaiting(true);
		
		ObjectChangeManager.markChanged(this);
		
		JsonParser jsonParser = new JsonParser();
		
		GateWayRelatedTask serverTask = new GateWayRelatedTask(ResourcesPlugin.getWorkspace().getRoot().getProject(project), new IGateWayServerTaskDelegate() {
			@Override
			public void terminated() {
								
			}
			
			@Override
			public void started(GateWayRelatedTask task) {
				
			}
		});
				
		org.eclipse.swt.widgets.Display currentDisplay = org.eclipse.swt.widgets.Display.getCurrent();
		
		serverTask.getServer().thenAcceptAsync((IServer server) -> {
			try {
				String jsonString = isDsDatasetEnabled() ? server.getDatasets(dsSearch, dsIsMine) : server.getCompetitions(dsSearch, dsIsMine);
				
				JsonElement jsonTree = jsonParser.parse(jsonString);
				
				JsonArray array = jsonTree.getAsJsonArray();
				
				
				ArrayList<DatasetTableElement> searchResult = new ArrayList<DatasetTableElement>();
				
				for(int i = 0; i < array.size(); i++) {
					DatasetTableElement item = new DatasetTableElement(array.get(i));
					
					searchResult.add(new DatasetTableElement(array.get(i)));
				}
				
				if(isDsDatasetEnabled()) {
					dsSearchResultDatasets = searchResult;
				} else {
					dsSearchResultCompetitions = searchResult;
				}
			} catch(Throwable t) {
				t.printStackTrace();
			}
			
			setWaiting(false);
						
			serverTask.terminate();
			
			currentDisplay.asyncExec(new Runnable() {
				public void run() {
					ObjectChangeManager.markChanged(KaggleDatasetParams.this);
				}
			});
		});
		
		TaskManager.perform(serverTask);
	}
}