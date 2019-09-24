package com.onpositive.datasets.visualisation.ui.views;

import java.util.function.Consumer;

import com.onpositive.musket.data.core.IAnalizeResults;

public interface IAnalisysEngine {

	public void perform(DataSetAnalisysRequest data,Consumer<IAnalizeResults>func,Consumer<Throwable>error);
	
	public void terminate();

	public PossibleAnalisisSpec getSpec();

}
