package com.onpositive.dside.ui;

public class HumanCaption {

	public static String getHumanCaption(String s) {
		if (s==null) {
			return "null";
		}
		return (s.substring(0,1).toUpperCase()+s.substring(1)).replace('_', ' ');
	}
}
