package com.onpositive.mmdetection.wrappers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.widgets.Display;

import com.onpositive.python.command.PyCommandBuilder;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class ModulePathExtractor {
	
	public static String extractModulepath(String moduleName, String pythonPath) {
		
		if(pythonPath == null) {
			pythonPath = "C:\\work\\musket\\classification_training_pipeline;C:\\work\\musket\\mmdetection_pipeline;C:\\work\\musket\\musket_core;C:\\work\\musket\\segmentation_training_pipeline;D:\\kostya_work\\mmdetection;D:\\kostya_work\\object-detection;D:\\kostya_work\\project_kaggle_fashion;C:\\work\\musket\\classification_training_pipeline\\classification_pipeline;C:\\work\\musket\\mmdetection_pipeline\\mmdetection_pipeline;C:\\work\\musket\\musket_core\\musket_core;C:\\work\\musket\\segmentation_training_pipeline\\segmentation_pipeline;D:\\kostya_work\\project_kaggle_fashion\\modules;D:\\kostya_work\\project_kaggle_fashion\\utils;C:\\Program Files\\JetBrains\\PyCharm Community Edition 2018.3.2\\helpers\\third_party\\thriftpy;C:\\Program Files\\JetBrains\\PyCharm Community Edition 2018.3.2\\helpers\\pydev;C:\\Users\\Пользователь\\.PyCharmCE2018.3\\system\\cythonExtensions;C:/work/musket/musket_core/musket_core";
		}
		
		String script = "import os\n" + 
				"import " + moduleName + "\n" + 
				"path = os.path.dirname(mmdet.__file__)\n" + 
				"print(path)";
		
		List<String> args = Arrays.asList(new String[] {"-c", script});
		ProcessBuilder command = PyCommandBuilder.buildCommand(args,pythonPath);
		
		try {
			command.redirectError(Redirect.PIPE);
			command.redirectOutput(Redirect.PIPE);
			command.redirectInput(Redirect.PIPE);
			Process process = command.start();
			
			InputStream es = process.getErrorStream();
			InputStream is = process.getInputStream();
			
			BufferedReader isReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			BufferedReader esReader = new BufferedReader(new InputStreamReader(es, "utf-8"));
			
			StringBuilder isBld = new StringBuilder();
			StringBuilder esBld = new StringBuilder();
			
			String str;
			while((str = isReader.readLine())!=null) {
				isBld.append(str);
			}
			while((str = esReader.readLine())!=null) {
				esBld.append(str);
			}
			
			int waitFor = process.waitFor();
			
			if (waitFor != 0 || esBld.length() != 0) {
//				List<String> readAllLines = Arrays.asList(new String[] { esBld.toString() });
//				PythonError pythonError = new PythonError(readAllLines);
//				Display.getDefault().asyncExec(new Runnable() {
//
//					@Override
//					public void run() {
//						boolean createObject = WidgetRegistry.createObject(new StackVisualizer(pythonError));
//						if (createObject) {
//							pythonError.open();
//						}
//					}
//				});
				System.out.println(esBld.toString());
				return null;
			}
			String result = isBld.toString().replace("\\", "/");
			
			return result;
		} catch (InterruptedException | IOException e) {
//			DSIDEUIPlugin.log(e);
			e.printStackTrace();
		}
		return null;
	}

}
