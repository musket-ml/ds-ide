package com.onpositive.musket_core;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.IMusketConstants;
import com.onpositive.yamledit.io.YamlIO;

public class ExperimentIO {
	public static Experiment duplicate(Experiment experiment, String newName) {
		IContainer[] findContainersForLocation = ResourcesPlugin.getWorkspace().getRoot()
				.findContainersForLocationURI(experiment.getPath().toFile().toURI());
		for (IContainer container : findContainersForLocation) {
			IFile file = container.getFile(new Path(IMusketConstants.MUSKET_CONFIG_FILE_NAME));
			if (file.exists()) {
				IContainer parent = container.getParent();
				IFolder folder = parent.getFolder(new Path(newName));
				if (!folder.exists()) {
					try {
						folder.create(true, true, new NullProgressMonitor());
						IPath append = folder.getFullPath().append(IMusketConstants.MUSKET_CONFIG_FILE_NAME);
						file.copy(append, true, new NullProgressMonitor());
						IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(append);
						String portableString = file2.getParent().getLocation().toPortableString();
						return new Experiment(portableString);
					} catch (CoreException e) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
					}
				} else {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
							"Experiment with this name already exist at:" + parent.getFullPath().toPortableString());
				}
			}
		}
		return null;
	}
	
	public static Map<String, Object> readConfig(String experimentPath) {
		File file = new File(experimentPath, IMusketConstants.MUSKET_CONFIG_FILE_NAME);
		try (FileReader fileReader = new FileReader(file)){
			return (Map<String, Object>) YamlIO.load(fileReader);
		} catch (Exception e) {
			return new HashMap<>();
		}
	}
	
	public static void backup(Experiment experiment, boolean copyWeights) {
		String path = experiment.getPathString();
		String projectPath = experiment.getProjectPath();
		File modules = new File(projectPath, "modules");
		File mcy = new File(projectPath, IMusketConstants.COMMON_CONFIG_NAME);
		File historyDir = getHistoryDir(path);
		historyDir.mkdirs();
		int maxNum = getLaunchCount(historyDir);
		maxNum++;
		File experimentHistory = new File(historyDir, "" + maxNum);
		experimentHistory.mkdirs();
		if (modules.exists()) {
			Utils.copyDir(modules, new File(experimentHistory, "modules"));
		}
		if (mcy.exists()) {
			Utils.copyDir(mcy, new File(experimentHistory, IMusketConstants.COMMON_CONFIG_NAME));
		}
		String name = new File(path).getName();
		File ed = new File(experimentHistory, name);
		ed.mkdir();
		Utils.copyDir(new File(path, IMusketConstants.MUSKET_CONFIG_FILE_NAME), new File(ed, IMusketConstants.MUSKET_CONFIG_FILE_NAME));

		experimentHistory = new File(historyDir, "" + (maxNum - 1));
		if (experimentHistory.exists()) {
			ed = new File(experimentHistory, name);
			File metrics = new File(path, "metrics");
			if (metrics.exists()) {
				Utils.copyDir(metrics, new File(ed, "metrics"));
			}
			metrics = new File(path, "examples");
			if (metrics.exists()) {
				Utils.copyDir(metrics, new File(ed, "examples"));
			}
			if (copyWeights) {
				metrics = new File(path, "weights");
				if (metrics.exists()) {
					Utils.copyDir(metrics, new File(ed, "weights"));
				}
			}
		}
	}
	
	protected static File getHistoryDir(String path) {
		return new File(path, ".history");
	}
	
	protected static int getLaunchCount(File file2) {
		File[] listFiles = file2.listFiles();
		int maxNum = -1;
		for (File f : listFiles) {
			try {
				String name = f.getName();
				int parseInt = Integer.parseInt(name);
				if (maxNum < parseInt) {
					maxNum = parseInt;
				}
			} catch (NumberFormatException e) {
				// Best effort
			}
		}
		return maxNum;
	}
	
	public static boolean delete(Experiment experiment) {
		IContainer[] findContainersForLocation = ResourcesPlugin.getWorkspace().getRoot()
				.findContainersForLocation(experiment.getPath());
		for (IContainer c : findContainersForLocation) {
			try {
				c.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				return false;
			}
		}
		return true;
	}
	
	public static ArrayList<ExperimentResults> results(String experimentPath) {
		ArrayList<ExperimentResults> resultList = new ArrayList<>();
		gatherResults("", experimentPath, resultList);
		return resultList;
	}
	
	private static void gatherResults(String baseName, String fullPath, ArrayList<ExperimentResults> r) {
		java.io.File currentDir = new java.io.File(fullPath);
		if (!currentDir.isDirectory()) {
			return;
		}
		File[] listFiles = currentDir.listFiles();
		for (File current : listFiles) {
			if (current.getName().equals("summary.yaml")) {
				r.add(new ExperimentResults(baseName, current.getAbsolutePath()));
			} else {
				if (current.getName().startsWith("trial")) {
					gatherResults("trial: " + current.getName(), current.getAbsolutePath(), r);
				}
				try {
					gatherResults(baseName + " split:" + current.getName(), current.getAbsolutePath(), r);
				} catch (Exception e) {
					DSIDEUIPlugin.log(e);
				}
			}
		}
	}
	public static boolean hasResults(String baseName, String fullPath) {
		java.io.File currentDir = new java.io.File(fullPath);
		if (!currentDir.isDirectory()) {
			return false;
		}
		File[] listFiles = currentDir.listFiles();
		for (File current : listFiles) {
			if (current.getName().equals("summary.yaml")) {
				return true;
			} else {
				if (current.getName().startsWith("trial") && hasResults("trial: " + current.getName(), current.getAbsolutePath())) {
					return true;
				}
				try {
					if (hasResults(baseName + " split:" + current.getName(), current.getAbsolutePath())) {
						return true;
					}
				} catch (Exception e) {
					DSIDEUIPlugin.log(e);
				}
			}
		}
		return false;
	}
	
	public static ArrayList<ExperimentLogs> logs(String experimentPath) {
		ArrayList<ExperimentLogs> resultList = new ArrayList<>();
		gatherLogs("", experimentPath, resultList);
		return resultList;
	}
	
	private static void gatherLogs(String baseName, String fullPath, ArrayList<ExperimentLogs> logsList) {
		java.io.File currentDir = new java.io.File(fullPath);
		if (!currentDir.isDirectory()) {
			return;
		}
		File[] listFiles = currentDir.listFiles();
		for (File current : listFiles) {
			if (current.getName().equals("metrics") && current.isDirectory()) {
				for (File file : current.listFiles()) {
					String name = file.getName();
					int indexOf = name.indexOf('-');
					if (indexOf != -1) {
						String[] split = name.substring(indexOf + 1).split("\\.");
						ExperimentLogs logs = new ExperimentLogs(baseName + " fold:" + split[0] + " stage:" + split[1],
								file.getAbsolutePath());
						logsList.add(logs);
					}
				}
			} else {
				if (current.getName().startsWith("trial")) {
					gatherLogs("trial: " + current.getName(), current.getAbsolutePath(), logsList);
				}
				try {
					gatherLogs(baseName + " split: " + current.getName(), current.getAbsolutePath(), logsList);
				} catch (Exception e) {
					DSIDEUIPlugin.log(e);
				}
			}
		}
	}
	
	public static boolean hasLogs(String baseName, String fullPath) {
		java.io.File currentDir = new java.io.File(fullPath);
		if (!currentDir.isDirectory()) {
			return false;
		}
		File[] listFiles = currentDir.listFiles();
		for (File current : listFiles) {
			if (current.getName().equals("metrics") && current.isDirectory()) {
				for (File file : current.listFiles()) {
					String name = file.getName();
					int indexOf = name.indexOf('-');
					if (indexOf != -1) {
						return true;
					}
				}
			} else {
				if (current.getName().startsWith("trial")) {
					if (hasLogs("trial: " + current.getName(), current.getAbsolutePath())) {
						return true;
					}
				}
				try {
					if (hasLogs(baseName + " split: " + current.getName(), current.getAbsolutePath())) {
						return true;
					}
				} catch (Exception e) {
					DSIDEUIPlugin.log(e);
				}
			}
		}
		return false;
	}

}
