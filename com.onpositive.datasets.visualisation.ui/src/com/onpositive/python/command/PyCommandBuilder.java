package com.onpositive.python.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
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
		
		String pyExe = whichResult.isEmpty() ? PYTHON_EXECUTABLE : PYTHON3_EXECUTABLE;
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

}
