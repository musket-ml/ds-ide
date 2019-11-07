package com.onpositive.dside.ui;

import com.google.gson.JsonElement;
import com.onpositive.dside.wizards.DataLink;

public class DatasetTableElement {
	public String ref;
	public String size;
	
	public DatasetTableElement(JsonElement element) {
		this.ref = element.getAsJsonObject().get("ref").getAsString();
		this.size = element.getAsJsonObject().get("size").getAsString();
	}
	
	public DatasetTableElement(DataLink dataLink) {
		this.ref = dataLink.parsedUrl();
		this.size = "unknown";
	}
}
