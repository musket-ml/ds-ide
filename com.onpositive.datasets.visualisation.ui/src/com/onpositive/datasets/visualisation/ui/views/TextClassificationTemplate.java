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

@Display("dlf/textClassificationTemplate.dlf")
public class TextClassificationTemplate extends GenericExperimentTemplate {

	@Caption("Commas separated list of word embeddings to use")
	@Required
	@RealmProvider(EmbeddingsRealmProvider.class)
	ArrayList<String> embeddings;

	@Caption("Maximum number of words to process")
	@Required
	protected int maxLen = 100;
	
	@Caption("Remove random words")
	protected boolean remove_random_words;
	@Caption("Add random words")
	protected boolean add_random_words;
	@Caption("Swap random words")
	protected boolean replace_random_words;

	
	
	public Collection<String>getEmbeddings(){
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList("dice","iou","map10"));
		return arrayList;
	}

	@Override
	public String finish() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					ClassificationTemplate.class.getResourceAsStream("/templates/textClassificationWizard.yaml.txt")));
			Stream<String> lines = bufferedReader.lines();

			String result = lines.collect(Collectors.joining(System.lineSeparator()));
			bufferedReader.close();

			ArrayList<String>augmentations=new ArrayList<>();
			if (this.add_random_words) {
				augmentations.add("        - remove_random_words: 0.05");
				augmentations.add("        - add_random_words: 0.05");
				augmentations.add("        - swap_random_words: 0.05");
			}
			String aug="";
			if (!augmentations.isEmpty()) {
				aug="  - augmentation:"+System.lineSeparator()+"      weights: ["+augmentations.stream().map(x->"1").collect(Collectors.joining(","))+"]";
				aug=aug+System.lineSeparator()+"      seed: 12";
				aug=aug+System.lineSeparator()+"      body:"+System.lineSeparator();
				for (String a:augmentations) {
					aug=aug+a+System.lineSeparator();
				}
			}
//			if (this.hFlip) {
//				augmentation=augmentation+"   Fliplr: 0.5"+System.lineSeparator();
//			}
//			if (this.vFlip) {
//				augmentation=augmentation+"   Flipud: 0.5"+System.lineSeparator();
//			}
			result = result.replace((CharSequence) "{aug}", "" + aug);
			result = result.replace((CharSequence) "{classes}", "" + this.numClasses);
			result = result.replace((CharSequence) "{activation}", "" + this.activation);
			result = result.replace((CharSequence) "{maxLen}", "" + this.maxLen);
			result = result.replace((CharSequence) "{embeddings}", "" + this.embeddings.stream().collect(Collectors.joining(",")));
			// result=result.replace((CharSequence)"{architecture}", ""+this.architecture);
			result = result.replace((CharSequence) "{loss}", "" + this.loss);
//			if (this.testTime) {
//				String value="";
//				if (this.hFlip) {
//					value="Horizontal";
//					if (this.vFlip) {
//						value="Horizontal_and_vertical";
//					}
//				}
//				result=result+System.lineSeparator()+"testTimeAugmentation: "+value+System.lineSeparator();
//			}
			return result;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
