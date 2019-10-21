package com.onpositive.datasets.visualisation.ui.views;

import java.util.function.Consumer;

import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;

public interface IAnalisysEngine {

	public void filter(DataSetAnalisysRequest data, Consumer<IDataSet> func, Consumer<Throwable> error); 
	
	public void perform(DataSetAnalisysRequest data,Consumer<IAnalizeResults>func,Consumer<Throwable>error);
	
	public void terminate();

	public PossibleAnalisisSpec getSpec();

}
