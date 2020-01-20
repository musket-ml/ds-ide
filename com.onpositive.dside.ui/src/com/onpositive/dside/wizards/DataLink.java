package com.onpositive.dside.wizards;

import java.util.HashMap;
import java.util.Map;

public class DataLink {
	
	public final DataLinkType type;
	public final String destination;
	public final String url;
	public final boolean force;
	
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
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataLink other = (DataLink) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
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
