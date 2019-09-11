package com.onpositive.dside.ui;

import com.google.gson.JsonElement;

public class DatasetTableElement {
	public String ref;
	public String size;
	
	public DatasetTableElement(JsonElement element) {
		this.ref = element.getAsJsonObject().get("ref").getAsString();
		this.size = element.getAsJsonObject().get("size").getAsString();
	}
}
