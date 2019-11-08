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
	Boolean dsEnabled = true;
	
	Boolean dsMyDatasets = false;
	Boolean dsMyCompetitions = false;
	
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
	
	@Caption("Register in project's metadata without downloading")
	boolean dsSkipDownload = false;
	
	public boolean isDsDatasetEnabled() {
		return dsEnabled && dsIsDataset;
	}
	
	public boolean isDsCompetitionEnabled() {
		return dsEnabled && dsIsCompetition;
	}
	
	public DatasetTableElement getItem() {
		return isDsDatasetEnabled() ? dsDatsetItem : dsCompetitionItem;
	}
	
	public void applyDataLink(DataLink dataLink) {
		if(DataLinkType.KAGGLE_DATASET.equals(dataLink.type)) {
			dsDatsetItem = new DatasetTableElement(dataLink);
			
			dsSearchResultDatasets.clear();			
			dsSearchResultDatasets.add(dsDatsetItem);
			
			dsIsDataset = true;
			dsIsCompetition = false;
			
			return;
		}
		
		if(DataLinkType.KAGGLE_COMPETITION.equals(dataLink.type)) {
			dsCompetitionItem = new DatasetTableElement(dataLink);
			
			dsSearchResultCompetitions.clear();
			dsSearchResultCompetitions.add(dsCompetitionItem);
			
			dsIsDataset = false;
			dsIsCompetition = true;
			
			return;
		}
	};
	
	public void deserializeFromJsonString(String jsonString) {
		JsonParser parser = new JsonParser();
		
		JsonElement root = parser.parse(jsonString);
		
		String type = root.getAsJsonObject().get("type").getAsString();
		
		if("dataset".equals(type)) {
			dsDatsetItem = new DatasetTableElement(root);
			
			dsSearchResultDatasets.add(dsDatsetItem);
			
			dsIsDataset = true;
			dsIsCompetition = false;
		} else if("competition".equals(type)) {
			dsCompetitionItem = new DatasetTableElement(root);
			
			dsSearchResultCompetitions.add(dsCompetitionItem);
			
			dsIsDataset = false;
			dsIsCompetition = true;
		}
	}
	
	public String serializeToJsonString() {
		JsonParser parser = new JsonParser();
		
		JsonElement root = parser.parse("{}");
		
		String type = dsIsDataset ? "dataset" : "competition";
		
		root.getAsJsonObject().addProperty("type", type);
		root.getAsJsonObject().addProperty("ref", getItem().ref);
		root.getAsJsonObject().addProperty("size", getItem().size);
		
		return root.toString();
	}
	
	public DataLink asDatalink() {
		String ref = dsIsDataset ? "kaggle.dataset://" : "kaggle.competition://";
		
		ref = ref + getItem().ref;
		
		return new DataLink(ref);
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
				String jsonString = isDsDatasetEnabled() ? server.getDatasets(dsSearch, dsMyDatasets) : server.getCompetitions(dsSearch, dsMyCompetitions);
				
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