package com.onpositive.musket.data.registry;

import java.io.File;
import java.util.HashMap;

import com.onpositive.musket.data.core.DataSetMemento;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetIO;
import com.onpositive.musket.data.core.IProgressMonitor;

public class FileIO implements IDataSetIO{

	public static HashMap<String, IDataSetIO>loaders=new HashMap<String, IDataSetIO>();
	
	static {
		loaders.put("csv", new CSVKind());
	}
	
	@Override
	public IDataSet loadDataSet(DataSetMemento memento, IProgressMonitor monitor) {
		String url=memento.getUrl();
		String path=url.substring(url.indexOf("://")+3);
		File file = new File(path);
		boolean exists = file.exists();
		if (!exists) {
			DataSetMemento original = memento.getOriginal();
			if (original==null) {
				throw new IllegalArgumentException("Data set not found at:"+path);
			}
			else {
				original.settings.put(DataSetMemento.TARGET, "file://"+path);
			}
			return DataSetIO.getInstance().loadDataSet(original, monitor);
		}
		String kind = getKind(memento, file);
		IDataSetIO iDataSetIO = loaders.get(kind);
		if (iDataSetIO==null) {
			throw new IllegalArgumentException("Unknown kind");
		}
		return iDataSetIO.loadDataSet(memento, monitor);
	}

	private String getKind(DataSetMemento memento, File file) {
		String kind = memento.getKind();
		if (kind==null) {
			if (file.getName().endsWith(".csv")) {
				kind="csv";
			}
		}
		return kind;
	}

	@Override
	public void saveDataset(DataSetMemento memento, IDataSet set, IProgressMonitor monitor) {
		String url=memento.getUrl();
		String path=url.substring(url.indexOf("://")+3);
		String kind = getKind(memento, new File(path));
		IDataSetIO iDataSetIO = loaders.get(kind);
		iDataSetIO.saveDataset(memento, set, monitor);
	}

}