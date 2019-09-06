package com.onpositive.dside.ui;

import com.google.gson.JsonElement;

public class DatasetTableElement {
	public String ref;
	public String url;
	
	public DatasetTableElement(JsonElement element) {
		this.ref = element.getAsJsonObject().get("ref").getAsString();
		this.url = element.getAsJsonObject().get("url").getAsString();
	}
}
