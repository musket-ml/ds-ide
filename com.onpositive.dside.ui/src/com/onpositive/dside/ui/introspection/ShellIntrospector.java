package com.onpositive.dside.ui.introspection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;

import com.onpositive.dside.dto.PythonError;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.musket_core.StackVisualizer;
import com.onpositive.python.command.PyCommandBuilder;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.yamledit.introspection.InstrospectionResult;
import com.onpositive.yamledit.io.YamlIO;

public class ShellIntrospector implements IIntrospector {

	private static final String PYTHON3_EXECUTABLE = "python3";
	private static final String PYTHON_EXECUTABLE = "python";

	@Override
	public InstrospectionResult introspect(String projectPath, String pythonPath, String resultMetaPath) {
		
		List<String> args = Arrays.asList(new String[] {"-m", "musket_core.inspectProject", "--project", projectPath, "--out", resultMetaPath});
		
		ProcessBuilder command = PyCommandBuilder.buildCommand(args, pythonPath);
		
		try {
			File resultMetaFile = new File(resultMetaPath);
			File resultMetaFolder = resultMetaFile.getParentFile();

			File file = new File(resultMetaFolder, "error.log");
			command.redirectError(file);
			command.redirectOutput(new File(resultMetaFolder, "output.log"));
			int waitFor = command.start().waitFor();
			
			if (waitFor != 0) {
				List<String> readAllLines = Files.readAllLines(file.toPath());
				PythonError pythonError = new PythonError(readAllLines);
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						boolean createObject = WidgetRegistry.createObject(new StackVisualizer(pythonError));
						if (createObject) {
							pythonError.open();
						}
					}
				});
				return null;
			}
			FileReader fileReader = new FileReader(resultMetaPath);
			try {
				InstrospectionResult loaded = YamlIO.loadAs(fileReader, InstrospectionResult.class);
				return loaded;

			} finally {
				fileReader.close();
			}
			// ServerManager.perform(new IntrospectTask(this));
		} catch (InterruptedException | IOException e) {
			DSIDEUIPlugin.log(e);
		}
		return null;
	}
}
