package com.onpositive.musket.data.column.analisys;

import java.util.Collection;
import com.onpositive.musket.data.table.IColumn;

public class NumericColumnFeatures extends BasicColumnFeatures{

	public final double minumum;
	
	public final double maximum;
	
	public final double mean;
	public final double std;
	
	
	public NumericColumnFeatures(double minumum, double maximum, double mean, double std) {
		super();
		this.minumum = minumum;
		this.maximum = maximum;
		this.mean = mean;
		this.std = std;
	}
	
	public static NumericColumnFeatures compute(IColumn column) {
		Collection<Object> values = column.values();
		for (Object o:values) {
			if (o instanceof String) {
				o=Double.parseDouble((String) o);
			}
			Number n=(Number) o;
		}
		return null;		
	}
}
