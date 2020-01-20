package com.onpositive.datasets.visualisation.ui.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/classificationTemplate.dlf")
public class ClassificationTemplate extends ImageExperimentTemplate{
	
	public ArrayList<String>getActivations(){
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("sigmoid");
		arrayList.add("softmax");
		return arrayList;
	}
	
	public String finish() {
		try {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassificationTemplate.class.getResourceAsStream("/templates/classificationWizard.yaml.txt")));
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
		result=result.replace((CharSequence)"{loss}", ""+this.loss);
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
		return result; 
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}