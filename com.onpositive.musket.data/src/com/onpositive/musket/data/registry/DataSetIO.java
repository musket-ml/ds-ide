package com.onpositive.musket.data.registry;

import java.util.HashMap;

import com.onpositive.musket.data.core.DataSetMemento;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetIO;
import com.onpositive.musket.data.core.IProgressMonitor;

public class DataSetIO implements IDataSetIO{

	protected HashMap<String, IDataSetIO>handlers=new HashMap<String, IDataSetIO>();
	
	public static IDataSetIO getInstance() {
		return new DataSetIO();
	}
	
	public DataSetIO() {
		handlers.put("file", new FileIO());
	}

	@Override
	public IDataSet loadDataSet(DataSetMemento memento, IProgressMonitor monitor) {
		IDataSetIO iDataSetIO = getInnerIO(memento);
		IDataSet loadDataSet = iDataSetIO.loadDataSet(memento, monitor);
		if (memento.getTarget()!=null) {
			iDataSetIO.saveDataset(memento.withTarget(), loadDataSet, monitor);
		}
		return loadDataSet;
	}

	private IDataSetIO getInnerIO(DataSetMemento memento) {
		String url = memento.getUrl();
		String scheme = url.substring(0,url.indexOf("://"));
		IDataSetIO iDataSetIO = handlers.get(scheme);
		if (iDataSetIO==null) {
			throw new IllegalArgumentException("Unknown scheme");
		}
		return iDataSetIO;
	}

	@Override
	public void saveDataset(DataSetMemento memento, IDataSet set, IProgressMonitor monitor) {
		IDataSetIO iDataSetIO = getInnerIO(memento);
		iDataSetIO.saveDataset(memento, set, monitor);
	}
	
//	public static IDataSet load(String url) {
//		DataSetMemento dataSetMemento = new DataSetMemento(url);
//		IDataSet loadDataSet = DataSetIO.getInstance().loadDataSet(dataSetMemento, null);
//		return loadDataSet;
//	}
	
	public static IDataSet load(String url,String encoding) {
		DataSetMemento dataSetMemento = new DataSetMemento(url);
		dataSetMemento.setEncoding(encoding);
		IDataSet loadDataSet = DataSetIO.getInstance().loadDataSet(dataSetMemento, null);
		return loadDataSet;
	}
}

