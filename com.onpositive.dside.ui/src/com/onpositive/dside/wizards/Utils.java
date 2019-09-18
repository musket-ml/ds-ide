package com.onpositive.dside.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Utils {
	private Utils() {
		
	}
	
	static String kaggleUserName() {
		File file = Path.fromOSString(System.getProperty("user.home")).append(".kaggle").append("kaggle.json").toFile();
		
		if(file.exists()) {
			JsonParser parser = new JsonParser();
			
			try {
				JsonElement json = parser.parse(new InputStreamReader(new FileInputStream(file)));
				
				return json.getAsJsonObject().get("username").getAsString();
			} catch (Throwable t) {
				t.printStackTrace();
				
				return "";
			}			
		}
		
		return "";
	}
}
