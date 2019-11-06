package com.onpositive.dside.ui.introspection;

import com.onpositive.python.command.IPythonPathProvider.PyInfo;
import com.onpositive.yamledit.introspection.InstrospectionResult;

public interface IIntrospector {
	
	InstrospectionResult introspect(String projectPath, PyInfo pythonPath, String absolutePath);
	
}
