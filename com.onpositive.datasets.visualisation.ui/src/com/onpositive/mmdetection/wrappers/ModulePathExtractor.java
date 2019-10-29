package com.onpositive.mmdetection.wrappers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	
	public static Data extractCheckpointsAndConfigs(String pythonPath) {
		String sep1 = "####";
		String sep2 = "@@";
		String script = String.format(
				"from mmdetection_pipeline import checkpoint_registry\n" +
				"for x in checkpoint_registry.listConfigs():\n" + 
				"    print(x.toString() + '%s')\n" +
				"print('%s')\n" +
				"for x in checkpoint_registry.listModelWeightPaths():\n" + 
				"    print(x['filename'] + '%s')", sep2,sep1,sep2);
		
		String str = PyCommandBuilder.executeScript(pythonPath, script);
		int ind = str.indexOf(sep1);
		String configsString = str.substring(0,ind).trim();
		String checkpointsString = str.substring(ind+sep1.length()).trim();		
		List<String> configDescriptions = Arrays.asList(configsString.split(sep2));
		List<String> checkpointNames = Arrays.asList(checkpointsString.split(sep2))
				.stream().filter(x->x!=null).collect(Collectors.toList());
		List<MMDetCfgData> configs = configDescriptions.stream()
				.map(x->ExampleExtractor.configFromDescriptionString(x)).filter(x->x!=null).collect(Collectors.toList());
		Data result = new Data(configs, checkpointNames);
		return result;
	}
	
	public static class Data{
		
		public Data(List<MMDetCfgData> configs, List<String> checkpointNames) {
			super();
			this.configs = configs;
			this.checkpointNames = checkpointNames;
		}

		private List<MMDetCfgData> configs;
		
		private List<String> checkpointNames;

		public List<MMDetCfgData> getConfigs() {
			return configs;
		}

		public List<String> getCheckpointNames() {
			return checkpointNames;
		}
	}


}
