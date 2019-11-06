package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.HashMap;

public class MultiOutputClasses {

	public static class ClassSettings{
	
		protected ArrayList<String>classes;
		
		protected HashMap<String, String>labels;
		
		protected boolean isExclusive;
	}
	
	protected ArrayList<ClassSettings>settings=new ArrayList<>();
	
	public MultiOutputClasses() {
		
	}
}
