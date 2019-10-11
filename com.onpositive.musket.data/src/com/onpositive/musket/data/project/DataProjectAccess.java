package com.onpositive.musket.data.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.IQuestionAnswerer;

public class DataProjectAccess {

	public static DataProject getProject(File file) {
		return new DataProject(file);		
	}
	
	public static IDataSet getDataSet(File file,IQuestionAnswerer answerer) {
		File dataRoot=findDataRoot(file);
		if (dataRoot==null) {
			dataRoot=file.getParentFile();
		}
		if (dataRoot!=null) {
			DataProject project = getProject(dataRoot);
			return project.getDataSet(file,answerer);
		}
		return null;		
	}

	private static File findDataRoot(File file) {
		File parentFile = file.getParentFile();
		if (parentFile==null) {
			return null;
		}
		if (new File(parentFile,"data").exists()) {
			return new File(parentFile,"data");
		}
		return findDataRoot(parentFile);
	}

	public static void updateMeta(File file2, IDataSet ds) {
		try {
			FileWriter fileWriter = new FileWriter(DataProject.getMetaFile(file2));
			Map<String, Object> settings = ds.getSettings();
			HashMap<String, Object>ns=new HashMap<>();
			settings.keySet().forEach(v->{
				ns.put(v, settings.get(v).toString());
			});
			new Yaml().dump(ns, fileWriter);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
