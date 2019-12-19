package com.onpositive.datasets.visualisation.ui.views;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IPythonStringGenerator;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class DataSetGenerator {

	private IDataSet dataSet;
	private String name;
	private boolean makePrimary;
	private File inputFile;
	private Object modelObject;

	public static void modifyFile(IFile file, Consumer<ArrayList<String>> modifier) {
		if (!file.exists()) {
			try {
				file.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
			} catch (CoreException e) {
				throw new IllegalStateException(e);
			}
		}
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getContents(true)));
			List<String> collect = bufferedReader.lines().collect(Collectors.toList());
			ArrayList<String> arrayList = new ArrayList<>(collect);
			modifier.accept(arrayList);
			String collect2 = arrayList.stream().collect(Collectors.joining(System.lineSeparator()));
			file.setContents(new ByteArrayInputStream(collect2.getBytes("UTF-8")), true, true,
					new NullProgressMonitor());
			try {
				bufferedReader.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		} catch (CoreException e) {
			throw new IllegalStateException(e);
	
		} catch (UnsupportedEncodingException e1) {
			throw new IllegalStateException(e1);
		}
	
	}

	public boolean generateDataSet(IDataSet dataSet, File inputFile, String name, boolean makePrimary, IProject project) {
		this.dataSet = dataSet;
		this.name = name;
		this.inputFile=inputFile;
		this.makePrimary = makePrimary;
		IPythonStringGenerator ps=(IPythonStringGenerator) dataSet;
		Object modelObject = ps.modelObject();
		if (modelObject!=null) {
			boolean createObject = WidgetRegistry.createObject(modelObject);
			if (!createObject) {
				return false;
			}
		}
		this.modelObject=modelObject;
		IFolder folder = getOrCreateFolder(project, "modules");
		modifyFile(folder.getFile("datasets.py"), this::addDataSet);
		modifyFile(project.getFile("common.yaml"), this::addDataDeclaration);
		return true;
	}

	protected IFolder getOrCreateFolder(IProject project, String name) {
		IFolder folder = project.getFolder(name);
		if (!folder.exists()) {
			try {
				folder.create(true, true, new NullProgressMonitor());
			} catch (CoreException e) {
				throw new IllegalStateException(e);
			}
		}
		return folder;
	}

	/// static HashSet<String>requiredImports=new HashSet<>();

	private void addDataSet(ArrayList<String> arrayList) {
		boolean found = false;
		IPythonStringGenerator pythonStringGenerator = this.dataSet.as(IPythonStringGenerator.class);
		for (String str : arrayList) {
			if (str.trim().equals(pythonStringGenerator.getImportString())) {
				found = true;
				break;
			}
		}
		if (!found) {
			arrayList.add(0, pythonStringGenerator.getImportString());
		}
		arrayList.add("");
		arrayList.add("@datasets.dataset_provider"+"(origin=\""+inputFile.getName()+"\",kind=\""+this.dataSet.getClass().getSimpleName()+"\""+")");
		arrayList.add("def get" + this.name + "():");
		File root=inputFile;
		while (!root.getName().equals("data")) {
			root=root.getParentFile();
		}
		String substring = inputFile.getAbsolutePath().substring(root.getAbsolutePath().length()+1);
		
		arrayList.add("    return " + pythonStringGenerator.generatePythonString(substring.replace('\\', '/'),modelObject));
	}

	private void addDataDeclaration(ArrayList<String> arrayList) {
		int index = 0;
		int id=-1;
		boolean hasDataSet=false;
		for (String s : arrayList) {
			if (s.trim().equals("datasets:")) {
				id=index;
			}
			if (s.trim().equals("dataset:")) {
				hasDataSet=true;
			}
			index = index + 1;
		}
		if (id==-1) {
			arrayList.add("datasets:");
			id=arrayList.size();
		}
		if (id>=arrayList.size()) {
			id=arrayList.size()-1;
		}
		arrayList.add(id+1,"    "+name+":");
		arrayList.add(id+2,"      "+"get"+name+": []");
		if (!hasDataSet) {
			arrayList.add("dataset:");
			arrayList.add("    "+"get"+name+": []");			
		}
		
	}
}
