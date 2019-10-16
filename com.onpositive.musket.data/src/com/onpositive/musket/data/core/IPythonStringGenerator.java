package com.onpositive.musket.data.core;

public interface IPythonStringGenerator {
	
	public Object modelObject();

	public String generatePythonString(String sourcePath,Object modelObject);
	
	public String getImportString();
}
