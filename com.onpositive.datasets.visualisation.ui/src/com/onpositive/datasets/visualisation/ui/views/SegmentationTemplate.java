package com.onpositive.datasets.visualisation.ui.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/segmentationTemplate.dlf")
public class SegmentationTemplate extends ImageExperimentTemplate{
	
	@Caption("Backbone")
	protected String backbone="resnet34";
	
	{
	architecture="Unet";
	}
	
	
	@Caption("Final metric")
	@Required
	protected String final_metric="dice";
	
	@Caption("Threat true negatives as")
	@RealmProvider(value=EnumRealmProvider.class)
	@Required
	protected TrueNegatives empty_is=TrueNegatives.ONE;
	
	@Caption("Optimize treshold")
	protected boolean optimizeTreshold=true;
	
	@Caption("Ignore true negative images")
	protected boolean ignoreNegatives=false;
	
	@Caption("Append second training stage with true negative images")
	protected boolean append=false;
	
	public static enum TrueNegatives{
		ONE,
		ZERO,
		SKIP
	}
	
	public Collection<String>getFinalmetrics(){
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList("dice","iou","map10"));
		return arrayList;
	}
	
	
	public String finish() {
		try {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(SegmentationTemplate.class.getResourceAsStream("/templates/segmentationWizard.yaml.txt")));
		Stream<String> lines = bufferedReader.lines();
		
		String result=lines.collect(Collectors.joining(System.lineSeparator()));
		bufferedReader.close();
		result=result.replace((CharSequence)"{width}", ""+this.width);
		String augmentation="";
		if (this.hFlip) {
			augmentation=augmentation+"   Fliplr: 0.5"+System.lineSeparator();
		}
		if (this.vFlip) {
			augmentation=augmentation+"   Flipud: 0.5"+System.lineSeparator();
		}
		result=result.replace((CharSequence)"{aug}", ""+augmentation);
		result=result.replace((CharSequence)"{height}", ""+this.height);
		result=result.replace((CharSequence)"{numClasses}", ""+this.numClasses);
		result=result.replace((CharSequence)"{activation}", ""+this.activation);
		result=result.replace((CharSequence)"{architecture}", ""+this.architecture);
		result=result.replace((CharSequence)"{backbone}", ""+this.backbone);
		String negatives="";
		if (this.ignoreNegatives) {
			negatives="negatives: 0";
		}
		result=result.replace((CharSequence)"{negatives}", ""+negatives);
		String secondStage="";
		if (this.append) {
			secondStage="  - epochs: 30 #let's go for 30 epochs\r\n" + 
					"    negatives: 2  \r\n" + 
					"    validation_negatives: 0";
		}
		result=result.replace((CharSequence)"{secondStage}", ""+secondStage);
		if (this.testTime) {
			String value="";
			if (this.hFlip) {
				value="Horizontal";
				if (this.vFlip) {
					value="Horizontal_and_vertical";
				}
			}
			result=result+System.lineSeparator()+"testTimeAugmentation: "+value+System.lineSeparator();
		}
		String final_metric=this.final_metric;
		if (this.optimizeTreshold) {
			final_metric=final_metric+"_with_custom_treshold";
		}
		else {
			final_metric=final_metric+"_";
		}
		final_metric=final_metric+"_true_negative_is_"+this.empty_is.toString().toLowerCase();
		result=result.replace((CharSequence)"{final_metric}", ""+final_metric);
		return result; 
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
	}
}