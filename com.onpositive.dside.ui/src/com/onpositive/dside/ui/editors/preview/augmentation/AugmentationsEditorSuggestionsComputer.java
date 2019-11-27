package com.onpositive.dside.ui.editors.preview.augmentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.editors.preview.AbstractSuggestionsComputer;
import com.onpositive.dside.ui.editors.preview.Suggestion;

public class AugmentationsEditorSuggestionsComputer extends AbstractSuggestionsComputer {
	
	private List<Suggestion> suggestionsList;

	private List<Suggestion> loadSuggestions() {
		List<Suggestion> suggestionsList = new ArrayList<>();
		URL res = DSIDEUIPlugin.getDefault().getBundle().getResource("/resources/docs/augmenters.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.openStream()))) {
			while(reader.ready()) {
				String line = reader.readLine().trim();
				String[] parts = line.split("\\|");
				if (parts.length == 2) {
					String title = parts[0];
					int idx = parts[0].indexOf('(');
					String text = idx > 0 ? parts[0].substring(0, idx).trim() : parts[0];
					suggestionsList.add(new Suggestion(text.trim(), title.trim(), parts[1].trim()));
				} else {
					DSIDEUIPlugin.log("Ivalid augmenter description line: " + line);
				}
			}
		} catch (IOException e) {
			DSIDEUIPlugin.log(e);
		}
		return suggestionsList;
	}

	@Override
	protected Collection<Suggestion> getAllSuggestions() {
		if (suggestionsList == null) {
			suggestionsList = loadSuggestions();
		}
		return suggestionsList;
	}

}
