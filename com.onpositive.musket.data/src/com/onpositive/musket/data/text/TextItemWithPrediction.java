package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.generic.StringUtils;
import com.onpositive.musket.data.images.IBinaryClassificationItemWithPrediction;
import com.onpositive.musket.data.table.ITabularItem;

public class TextItemWithPrediction extends TextItem implements IBinaryClassificationItemWithPrediction{
	

	private TextItem prediction;
	
	public TextItemWithPrediction(AbstractTextDataSet textDataSet, ITabularItem baseItem,TextItem prediction) {
		super(textDataSet, baseItem);
		this.prediction=prediction;		
	}

	@Override
	public boolean isPredictionPositive() {
		return prediction.isPositive();
	}

	@Override
	public IItem getPrediction() {
		return prediction;
	} 

	protected String getClassText() {
		ArrayList<String> classes = this.classes();
		
		ArrayList<String> classes2 = this.prediction.classes();
		if (!new HashSet<>(classes).equals(new HashSet<>(classes2))) {
			StringBuilder bld=new StringBuilder();
			bld.append("<FONT COLOR=BLUE>" + StringUtils.encodeHtml(classes.stream().collect(Collectors.joining(", "))) + "</FONT>");
			
			bld.append(" &lt;&gt; <FONT COLOR=RED>" + StringUtils.encodeHtml(classes2.stream().collect(Collectors.joining(", "))) + "</FONT>");	
			bld.append(" ");
			String string = "<html>" + bld.toString() + "</html>";
			return string;
			//we have some interesting differences
		}
		String string = "<html>" + classes.stream().collect(Collectors.joining(", ")) + "</html>";
		return string;
	}
}
