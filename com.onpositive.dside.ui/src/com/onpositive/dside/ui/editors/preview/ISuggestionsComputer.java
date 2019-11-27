package com.onpositive.dside.ui.editors.preview;

import java.util.Collection;

public interface ISuggestionsComputer {

	/**
	 * Calculates the resulting proposals for given offset.
	 * 
	 * @param source Source text
	 * @param offset Offset, where completion was called
	 * @return proposals, never <code>null</code>
	 */
	public Collection<Suggestion> calculate(String source, int offset);
	
	/**
	 * Resolves text before given offset
	 * 
	 * @param source
	 * @param offset
	 * @return text, never <code>null</code>
	 */
	public String getTextBefore(String source, int offset);
	
}
