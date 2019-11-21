package com.onpositive.python.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class PyCommandBuilder {

	private static final String PYTHON3_EXECUTABLE = "python3";
	private static final String PYTHON_EXECUTABLE = "python";


	public static ProcessBuilder buildCommand(Collection<String> args, String pythonPath) {
		String whichCommand = StringUtils.stripToEmpty(System.getProperty("os.name")).startsWith("Windows") ? "where" : "which";
		String whichResult = "";
		
		try {
			whichResult = runProcess(new ProcessBuilder().command(whichCommand, PYTHON3_EXECUTABLE).start());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		String pyExe = PYTHON_EXECUTABLE;
		ArrayList<String> fullArgsList = new ArrayList<>(1 + args.size());
		fullArgsList.add(pyExe);
		fullArgsList.addAll(args);
		String[] fullArgsArr = fullArgsList.toArray(new String[fullArgsList.size()]);		
		ProcessBuilder command = new ProcessBuilder().command(fullArgsArr);
		
		Map<String, String> envs = System.getenv();		
		command.environment().putAll(envs);
		
		if (pythonPath != null) {
			command.environment().put("PYTHONPATH", pythonPath);
		}
		return command;
	}
	
	private static String runProcess(Process process) throws Throwable {
		process.waitFor();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		StringBuilder builder = new StringBuilder();
		
		String line = null;
		
		while((line = reader.readLine()) != null) {
			builder.append(line);
			
			builder.append(System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}


	public static String executeScript(String pythonPath, String script) {
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
