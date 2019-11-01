package com.onpositive.dside.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.eclipse.core.runtime.IPath;

import com.onpositive.dside.ui.DSIDEUIPlugin;

public class LaunchScriptFileProvider {
	
	private static final String START_SCRIPT_FILE = "start_musket.py";
	
	public static File getScriptFile() {
		IPath stateLocation = DSIDEUIPlugin.getDefault().getStateLocation();
		File scriptFile  = new File(stateLocation.toFile(), START_SCRIPT_FILE);
		if (!scriptFile.exists()) {
			URL resource = DSIDEUIPlugin.getDefault().getBundle().getResource("scripts/" + START_SCRIPT_FILE);
			try {
				Files.copy(resource.openStream(), scriptFile.toPath());
			} catch (IOException e) {
				DSIDEUIPlugin.log(e);
			}
		}
		return scriptFile;
	}
}
