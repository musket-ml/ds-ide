/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package com.onpositive.dside.ui.editors.preview;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public abstract class AbstractSuggestionsComputer implements ISuggestionsComputer {

	/**
	 * Calculates the resulting proposals for given offset.
	 * 
	 * @param source Source text
	 * @param offset Offset, where completion was called
	 * @return proposals, never <code>null</code>
	 */
	public Collection<Suggestion> calculate(String source, int offset) {
		String wanted = getTextBefore(source, offset);
		return filter(getAllSuggestions(), wanted);
	}

	protected abstract Collection<Suggestion> getAllSuggestions();

	/**
	 * Resolves text before given offset
	 * 
	 * @param source
	 * @param offset
	 * @return text, never <code>null</code>
	 */
	public String getTextBefore(String source, int offset) {
		if (source == null || source.isEmpty()) {
			return "";
		}
		if (offset <= 0) {
			return "";
		}
		int sourceLength = source.length();
		if (offset > sourceLength) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int current = offset - 1; // -1 because we want the char before
		boolean ongoing = false;
		do {
			if (current < 0) {
				break;
			}
			char c = source.charAt(current--);
			ongoing = !Character.isWhitespace(c);
			if (ongoing) {
				sb.insert(0, c);
			}
		} while (ongoing);

		return sb.toString();
	}


	protected Collection<Suggestion> filter(Collection<Suggestion> allSuggestions, String wanted) {
		if (wanted == null || wanted.isEmpty()) {
			return allSuggestions;
		}
		LinkedHashSet<Suggestion> filtered = new LinkedHashSet<>();
		LinkedHashSet<Suggestion> addAfterEnd = new LinkedHashSet<>();
		String wantedLowerCase = wanted.toLowerCase();

		for (Suggestion suggestion : allSuggestions) {
			String wordLowerCase = suggestion.text.toLowerCase();
			if (wordLowerCase.startsWith(wantedLowerCase)) {
				filtered.add(suggestion);
			} else if (wordLowerCase.indexOf(wantedLowerCase) != -1) {
				addAfterEnd.add(suggestion);
			}
		}
		filtered.addAll(addAfterEnd);
		return filtered;
	}

}
