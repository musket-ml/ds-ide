package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.onpositive.musket.data.core.ChartData;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.VisualizationSpec;
import com.onpositive.musket.data.core.VisualizationSpec.ChartType;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Multiclass F1")
public class F1Analizer extends BinaryConfusionMatrix{

	private List<String> classes;
	
	static class Stat{
		int truepositive;
		int falsepositive;
		int falsenegative;
		int truenegative;
		
		public double precision() {
			if (truepositive+falsepositive==0) {
				return 0;
			}
			return truepositive/((double)(truepositive+falsepositive));
		}
		public double recall() {
			if (truepositive+falsenegative==0) {
				return 0;
			}
			return truepositive/((double)(truepositive+falsenegative));
		}
		public double f1() {
			if (truepositive+falsepositive==0) {
				return 0;
			}
			if (truepositive+falsenegative==0) {
				return 0;
			}
			return  2*precision()*recall()/(precision()+recall());
		}
	}
	
	protected LinkedHashMap<String, Stat>stat=new LinkedHashMap(); 

	@Override
	public IAnalizeResults analize(IDataSet ds) {
		if (ds instanceof IMulticlassClassificationDataSet) {
			IMulticlassClassificationDataSet ms=(IMulticlassClassificationDataSet) ds;
			classes=ms.classNames();
		}
		else if (ds instanceof IBinaryClassificationDataSet) {
			classes=new ArrayList<>();
			classes.add("pos");
		}
		for (String s:classes) {
			stat.put(s,new Stat());
		}
		return super.analize(ds);
	}
	@Override
	protected Object group(IItem v) {
		
		IBinaryClassificationItemWithPrediction b1=(IBinaryClassificationItemWithPrediction) v;
		if (v instanceof IMulticlassClassificationItem) {
			IMulticlassClassificationItem m1=(IMulticlassClassificationItem) v;
			IMulticlassClassificationItem item=(IMulticlassClassificationItem) m1;
			ArrayList<String> classes = item.classes();
			
			IMulticlassClassificationItem prediction=(IMulticlassClassificationItem) b1.getPrediction();
			ArrayList<String> classes2 = prediction.classes();			
			HashSet<String> gt = new HashSet<>(classes);
			HashSet<String> pr = new HashSet<>(classes2);			
			count(gt,pr);
		}
		else {
			HashSet<String>gt=new HashSet<>();
			HashSet<String> pr = new HashSet<>();
			if (b1.isPositive()) {
				gt.add("pos");
			}
			if (b1.isPredictionPositive()) {
				pr.add("pos");
			}
		}
		return super.group(v);
	}
	
	private void count(HashSet<String> gt, HashSet<String> pr) {
		for (String s:classes) {
			Stat stat2 = stat.get(s);
			if (gt.contains(s)) {
				if (pr.contains(s)) {
					stat2.truepositive++;
				}
				else {
					stat2.falsenegative++;
				}
			}
			else {
				if (pr.contains(s)) {
					stat2.falsepositive++;
				}
				else {
					stat2.truenegative++;
				}
			}
		}
	}

	@Override
	protected VisualizationSpec getVisualizationSpec() {
		VisualizationSpec visualizationSpec = super.getVisualizationSpec();
		ChartData.BasicChartData basicChartData = new ChartData.BasicChartData();
		visualizationSpec.type=ChartType.BAR;
		for (String s:classes) {
			basicChartData.values.put(s,func(s));
		}
		visualizationSpec.chart=basicChartData;
		return visualizationSpec;
	}
	protected double func(String s) {
		return stat.get(s).f1();
	}
}
