package com.onpositive.musket.data.core;

public interface IDataSetWithGroundTruth extends IDataSet{

	IItem getPrediction(int num);
}
