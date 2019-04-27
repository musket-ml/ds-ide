package com.onpositive.musket_core;

import java.text.NumberFormat;
import java.util.Map;

public class Score implements Comparable<Score>{
	
	double mean;
	double max;
	double min;
	double value;
	String representation;
	private Experiment experiment;
	private boolean real;
	
	public Score(Object o, Experiment experiment) {
		this.experiment=experiment;
		if (o instanceof Map<?,?>) {
			Map<String,?>m=(Map<String, ?>) o;
			Double max =(Double) m.get("max");
			if (max!=null) {
				initFromMeanMax(m);
			}
			else {
				String primaryMetric = experiment.getPrimaryMetric();
				if (primaryMetric.startsWith("val_")) {
					primaryMetric=primaryMetric.substring(4);
				}
				Double sc=null;
				if (m.containsKey("allStages")) {
					Object x1 = m.get("allStages");
					Map<String,Object>obj=(Map)x1;					
					if (obj.containsKey(primaryMetric+"_holdout")) {
						sc=(Double)obj.get(primaryMetric+"_holdout");
					}
					else if (obj.containsKey(primaryMetric)) {
						Object object = obj.get(primaryMetric);
						if (object instanceof Map) {
							initFromMeanMax((Map<String, ?>) object);
							return ;
						}
						else {
						sc=(Double)object;
						}
						
					}
					initFromScalar(sc);					
				}
				else {
					initFromScalar("Unknown metric");
				}
			}
		}
		else {
			initFromScalar(o);
		}
	}

	private void initFromScalar(Object o) {
		if (o == null) {
			this.representation="Empty Summary";
			this.value=-Double.MAX_VALUE+1;
		}	
		else if (o instanceof Number) {
			this.value=((Number)o).doubleValue();
			NumberFormat instance = NumberFormat.getInstance();
			instance.setMaximumFractionDigits(4);
			this.representation=instance.format(o);
			this.real=true;
		}
		else {
			this.representation=o.toString();
			this.value=-Double.MAX_VALUE+1;
			this.real=false;
		}
	}

	private void initFromMeanMax(Map<String, ?> m) {
		Double x =(Double) m.get("max");
		Double x1 = (Double) m.get("mean");
		Double x2 = (Double) m.get("min");
		this.value=x1;
		this.real=true;
		NumberFormat instance = NumberFormat.getInstance();
		instance.setMaximumFractionDigits(4);
		this.representation= "[" + instance.format(x2) + ", " + instance.format(x1) + ", " + instance.format(x)
				+ "]";
	}

	public Score(String representation) {
		this.representation=representation;
		this.value=-Double.MAX_VALUE/2;
		this.real=false;
	}

	@Override
	public int compareTo(Score o) {
		if (this.value>o.value) {
			return 1;
		}
		if (o.value>this.value) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return representation;
	}

	public boolean isRealScore() {		
		return this.real;
	}

}
