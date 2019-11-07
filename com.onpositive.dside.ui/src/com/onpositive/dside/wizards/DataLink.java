package com.onpositive.dside.wizards;

import java.util.HashMap;
import java.util.Map;

public class DataLink {
	DataLinkType type;
	
	public String destination;
	
	public String url;
	
	public boolean force;
	
	public DataLink(Object item) {
		if(item instanceof String) {
			this.url = (String) item;
			
			this.destination = null;
			
			this.force = false;
			
			this.type = DataLinkType.fromUrl(this.url);
		} else if(item instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) item;
			
			this.url = (String) map.getOrDefault("url", null);
			this.destination = (String) map.getOrDefault("destination", null);
			this.force = (boolean) map.getOrDefault("force", false);
			
			this.type = DataLinkType.fromUrl(this.url);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		DataLink another = (DataLink) obj;
		
		if(another == null) {
			return false;
		}
		
		if(another.url == null) {
			return false;
		}
		
		return another.url.equals(this.url);
	}
	
	public boolean isSimple() {
		if(destination != null) {
			return false;
		}
		
		if(force) {
			return false;
		}
		
		return true;
	}
	
	public Object serialize() {
		if(isSimple()) {
			return this.url;
		}
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("url", this.url);
		
		if(this.destination != null) {
			result.put("destination", this.destination);
		}
		
		if(this.force) {
			result.put("force", this.force);
		}
		
		return result;
	}
	
	public boolean isKaggle() {
		return DataLinkType.KAGGLE_COMPETITION.equals(this.type) || DataLinkType.KAGGLE_DATASET.equals(this.type);
	}
	
	public String parsedUrl() {
		if(this.url == null) {
			return null;
		}
		
		if(DataLinkType.HTTP.equals(this.type)) {
			return this.url;
		}
		
		return this.url.replaceFirst(this.type.getName() + "//:", "");
	}
}
