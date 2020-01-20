package com.onpositive.dside.ui.editors.preview.augmentation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.onpositive.dside.ui.editors.IExperimentConfigEditor;
import com.onpositive.dside.ui.editors.preview.AbstractSuggestionsComputer;
import com.onpositive.dside.ui.editors.preview.Suggestion;
import com.onpositive.yamledit.introspection.InstrospectedFeature;
import com.onpositive.yamledit.introspection.InstrospectionResult;

public class IntrospectedAugmentationsrSuggestionsComputer extends AbstractSuggestionsComputer {
	
	private static final String POSTFIX = ":";
	private List<Suggestion> suggestionsList;
	private IExperimentConfigEditor editor;

	public IntrospectedAugmentationsrSuggestionsComputer(IExperimentConfigEditor editor) {
		this.editor = editor;
	}

	private List<Suggestion> loadSuggestions() {
		InstrospectionResult details = editor.getProject().getDetails();
		return details.getFeatures().stream().filter(feature -> "Augmenter".equals(feature.getKind())).map(feature -> toSuggestion(feature)).collect(Collectors.toList());
	}

	protected Suggestion toSuggestion(InstrospectedFeature feature) {
		String text = feature.getName() + POSTFIX;
		String paramsStr = feature.getParameters().stream().map(param -> param.getName()).collect(Collectors.joining(","));
		String description = StringUtils.abbreviate(feature.getDoc(), 400);
		return new Suggestion(text, feature.getName() + "(" + paramsStr + ")", description);
	}

	@Override
	protected Collection<Suggestion> getAllSuggestions() {
		if (suggestionsList == null) {
			suggestionsList = loadSuggestions();
		}
		return suggestionsList;
	}

}
