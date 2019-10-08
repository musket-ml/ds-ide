package com.onpositive.dside.ui.introspection;

import com.onpositive.yamledit.introspection.InstrospectionResult;

public interface IIntrospector {
	
	InstrospectionResult introspect(String projectPath, String pythonPath, String absolutePath);
	
}
