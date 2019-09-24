package com.onpositive.musket.data.core;


public interface IDataSetIO {

	public IDataSet loadDataSet(DataSetMemento memento,IProgressMonitor monitor);

	public void saveDataset(DataSetMemento memento,IDataSet set,IProgressMonitor monitor);

}
