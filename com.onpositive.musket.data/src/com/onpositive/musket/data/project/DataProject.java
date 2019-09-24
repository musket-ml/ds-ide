package com.onpositive.musket.data.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ImageDataSetFactories;
import com.onpositive.musket.data.table.ImageRepresenter;

public class DataProject {

	private File file;

	private ImageRepresenter imageRepresenter;

	public DataProject(File file) {
		this.file = file;
		imageRepresenter = new ImageRepresenter(file.getAbsolutePath());
		imageRepresenter.configure();
	}

	@SuppressWarnings("unchecked")
	public IDataSet getDataSet(File file2) {
		ITabularDataSet t1 = DataSetIO.load("file://" + file2.getAbsolutePath()).as(ITabularDataSet.class);
		if (getMetaFile(file2).exists()) {
			try {
			FileReader fileReader = new FileReader(getMetaFile(file2));
			@SuppressWarnings("rawtypes")
			Map loadAs = new Yaml().loadAs(fileReader, Map.class);
			fileReader.close();
			IDataSet create = new ImageDataSetFactories(imageRepresenter).create(t1,loadAs);
			
			return create;
			}catch (Exception e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		} else {
			IDataSet create = new ImageDataSetFactories(imageRepresenter).create(t1);
			if (create != null) {
				try {
					FileWriter fileWriter = new FileWriter(getMetaFile(file2));
					new Yaml().dump(create.getSettings(), fileWriter);
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return create;
		}
	}

	static File getMetaFile(File file2) {
		return new File(file2.getParentFile(),"."+file2.getName() + ".dataset_desc");
	}

}
