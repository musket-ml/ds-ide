package com.onpositive.datasets.visualisation.ui.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

@Display("dlf/textLabelingTemplate.dlf")
public class TextSequenceTemplate extends GenericExperimentTemplate {

	@Caption("Commas separated list of word embeddings")
	@RealmProvider(EmbeddingsRealmProvider.class)
	ArrayList<String> embeddings;
	
	@Caption("Path to saved Google Bert checkpoint")
	@RealmProvider(BertRealmProvider.class)
	String bertPath;

	@Caption("Maximum number of words to process")
	@Required
	protected int maxLen = 100;
	
//	@Caption("Remove random words")
//	protected boolean remove_random_words;
//	@Caption("Add random words")
//	protected boolean add_random_words;
//	@Caption("Swap random words")
//	protected boolean replace_random_words;

	
	@Caption("CNN")
	protected boolean cnn_classifier;
	
	@Caption("RNN")
	protected boolean rnn_classifier=true;
	
//	@Caption("Google Bert")
//	protected boolean bert_classifier=false;
	
	@Caption("Add CRF")
	protected boolean add_crf=true;

	@Caption("Add dropout")
	private boolean dropout=true;
	
	public String getCaption() {
		return "AAAA";
	}

	@Override
	public String finish() {
		try {
			String string = "/templates/textLabeling.yaml.txt";
			if (this.cnn_classifier) {
				string = "/templates/textLabelingCNN.yaml.txt";
			}
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					ClassificationTemplate.class.getResourceAsStream(string)));
			Stream<String> lines = bufferedReader.lines();

			String result = lines.collect(Collectors.joining(System.lineSeparator()));
			bufferedReader.close();

			ArrayList<String>augmentations=new ArrayList<>();
			
			String aug="";
			if (!augmentations.isEmpty()) {
				aug="  - augmentation:"+System.lineSeparator()+"      weights: ["+augmentations.stream().map(x->"1").collect(Collectors.joining(","))+"]";
				aug=aug+System.lineSeparator()+"      seed: 12";
				aug=aug+System.lineSeparator()+"      body:"+System.lineSeparator();
				for (String a:augmentations) {
					aug=aug+a+System.lineSeparator();
				}
			}

			result = result.replace((CharSequence) "{aug}", "" + aug);
			result = result.replace((CharSequence) "{classes}", "" + this.numClasses);
			result = result.replace((CharSequence) "{activation}", "" + this.activation);

			result = result.replace((CharSequence) "{maxLen}", "" + this.maxLen);
			
			result = result.replace((CharSequence) "{embeddings}", "" + this.embeddings.stream().collect(Collectors.joining(",")));
			
			result = result.replace((CharSequence) "{loss}", "" + this.loss);
			String head="";
if (this.dropout) {			
	head=head+	"       - dropout: 0.4       \r\n";
}
if (this.add_crf) {
	head=head+	"       - CRF: [ "+this.numClasses+" ]";
}
else {
	head=head+	"       - conv1D: ["+this.numClasses+",1,softmax]";
}
			result = result.replace((CharSequence) "{head}", "" + head);
			return result;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}