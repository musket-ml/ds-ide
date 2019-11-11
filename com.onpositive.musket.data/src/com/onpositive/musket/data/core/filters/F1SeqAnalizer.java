package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.musket.data.core.ChartData;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.VisualizationSpec;
import com.onpositive.musket.data.core.VisualizationSpec.ChartType;
import com.onpositive.musket.data.text.Document;
import com.onpositive.musket.data.text.DocumentWithPredictions;
import com.onpositive.musket.data.text.Sentence;
import com.onpositive.musket.data.text.TextSequenceDataSetWithPredictions;
import com.onpositive.musket.data.text.Token;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("Multiclass F1")
public class F1SeqAnalizer extends SequenceMatchAnalizer implements IAnalizer<TextSequenceDataSetWithPredictions>{

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
		TextSequenceDataSetWithPredictions tf=(TextSequenceDataSetWithPredictions) ds;
		LinkedHashSet<String> linkedHashSet = tf.classGroups().get(0);
		classes=new ArrayList<>(linkedHashSet);
		Collections.sort(classes);
		for (String s:classes) {
			stat.put(s,new Stat());
		}
		return super.analize(ds);
	}
	
	@Override
	protected Object group(IItem v) {
		DocumentWithPredictions d=(DocumentWithPredictions) v;
		ArrayList<Sentence> contents = d.getContents();
		
		Document predictions = d.getPredictions();
		ArrayList<Sentence> contents2 = predictions.getContents();
		
		int size = contents.size();
		for (int i=0;i<size;i++) {
			Sentence sentence = contents.get(i);
			Sentence sentence2 = contents2.get(i);
			process(sentence,sentence2);
		}
		return super.group(v);
	}
	
	private void process(Sentence sentence, Sentence sentence2) {
		ArrayList<Token> tokens = sentence.tokens();
		ArrayList<Token> tokens2 = sentence2.tokens();
		int size = tokens.size();
		for (int i=0;i<size;i++) {
			HashSet<String>gt=new HashSet<>();
			HashSet<String>pr=new HashSet<>();
			Token token = tokens.get(i);
			gt.addAll(token.classes());
			if (i>=tokens2.size()) {
				pr.add("Not matched");
			}
			else {
				pr.addAll(tokens2.get(i).classes());
			}
			count(gt, pr);
		}
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
		VisualizationSpec visualizationSpec =new VisualizationSpec(getYName(), "Class", ChartType.PIE);
		ChartData.BasicChartData basicChartData = new ChartData.BasicChartData();
		visualizationSpec.type=ChartType.BAR;
		for (String s:classes) {
			basicChartData.values.put(s,func(s));
		}
		visualizationSpec.chart=basicChartData;
		if (classes.size()>50) {
			visualizationSpec.type=ChartType.TABLE;
		}
		visualizationSpec.full=basicChartData;
		return visualizationSpec;
	}
	
	protected String getYName() {
		return "F1";
	}
	protected double func(String s) {
		return stat.get(s).f1();
	}
}
