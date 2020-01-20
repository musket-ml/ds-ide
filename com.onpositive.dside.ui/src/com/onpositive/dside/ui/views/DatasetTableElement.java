package com.onpositive.dside.ui.views;

import com.google.gson.JsonElement;
import com.onpositive.dside.wizards.DataLink;

public class DatasetTableElement {
	
	private static final String PROTOCOL_SEPARATOR = "://";
	public final String ref;
	public final String size;
	
	public DatasetTableElement(JsonElement element) {
		this.ref = removeProtocol(element.getAsJsonObject().get("ref").getAsString());
		this.size = element.getAsJsonObject().get("size").getAsString();
	}

	public DatasetTableElement(DataLink dataLink) {
		this.ref = removeProtocol(dataLink.parsedUrl());

		this.size = "unknown";
	}
	
	private String removeProtocol(String dsUrl) {
		int idx = dsUrl.lastIndexOf(PROTOCOL_SEPARATOR);
		if (idx >= 0) { 
			dsUrl = dsUrl.substring(idx + PROTOCOL_SEPARATOR.length());
		}
		return dsUrl;
	}
}
