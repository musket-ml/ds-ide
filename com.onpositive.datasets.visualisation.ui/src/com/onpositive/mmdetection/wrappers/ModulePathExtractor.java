package com.onpositive.mmdetection.wrappers;

import java.util.Arrays;
import java.util.List;
import com.onpositive.python.command.PyCommandBuilder;

public class ModulePathExtractor {
	
	public static String extractModulepath(String moduleName, String pythonPath) {

		String script = "import os\n" + 
				"import " + moduleName + "\n" + 
				"path = os.path.dirname(" + moduleName + ".__file__)\n" + 
				"print(path)";
		
		return PyCommandBuilder.executeScript(pythonPath, script);
	}
	
	public static List<String> extractCheckpointFilenames(String pythonPath) {
		
		String script = "from mmdetection_pipeline import checkpoint_registry\n" + 
				"for x in checkpoint_registry.listModelWeightPaths():\n" + 
				"    print(x['filename'] + ';')";
		
		String str = PyCommandBuilder.executeScript(pythonPath, script);
		List<String> result = Arrays.asList(str.split(";"));
		return result;
	}


}
