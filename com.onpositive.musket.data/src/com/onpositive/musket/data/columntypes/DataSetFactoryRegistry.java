package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.musket.data.table.ImageDataSetFactories;
import com.onpositive.musket.data.text.SequenceLabelingFactories;
import com.onpositive.musket.data.text.TextDataSetFactories;

public class DataSetFactoryRegistry {

	private static DataSetFactoryRegistry registry;
	
	protected ArrayList<IDataSetFactory>factories=new ArrayList<>();
	
	public DataSetFactoryRegistry() {
		factories.add(new ImageDataSetFactories());
		factories.add(new TextDataSetFactories());
		factories.add(new SequenceLabelingFactories());
	}

	public List<IDataSetFactory>knownFactories(){
		return factories;		
	}
	
	public ArrayList<IDataSetFactory>matching(DataSetSpec spec){
		HashMap<IDataSetFactory, Double>scores=new HashMap<>();
		ArrayList<IDataSetFactory>results=new ArrayList<>();
		for (IDataSetFactory f:knownFactories()) {
			double estimate = f.estimate(spec);
			if (estimate>0) {
				scores.put(f, estimate);
				results.add(f);
			}
		}
		results.sort((x,y)->{
			double double1 = scores.get(x);
			double double2 = scores.get(y);
			if (double1>double2) {
				return 1;
			}
			if (double2>double1) {
				return 0;
			}
			return 0;
		});
		return results;		
	}
	
	public static DataSetFactoryRegistry getInstance() {
		if (registry==null) {
			registry=new DataSetFactoryRegistry();
		}
		return registry;
	}
}
