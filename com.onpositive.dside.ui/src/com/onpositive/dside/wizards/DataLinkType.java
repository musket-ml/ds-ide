package com.onpositive.dside.wizards;

import java.util.Arrays;

public enum DataLinkType {
	KAGGLE_DATASET("kaggle.dataset"),
	KAGGLE_COMPETITION("kaggle.competition"),
	HTTP("http");
	
	private String name;
	
	private DataLinkType(String name) {
        this.name = name;
    }
	
	public String getName() {
		return this.name;
	}
	
	public static DataLinkType fromUrl(String url) {
		if(url == null) {
			return null;
		}
		
		for(DataLinkType t: Arrays.asList(DataLinkType.values())) {
			if(url.startsWith(t.getName())) {
				return t;
			}
		}
		
		return null;
	};
}
