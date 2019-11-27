package com.onpositive.dside.ui.editors.preview;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.BoldStylerProvider;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension7;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.jcup.yamleditor.YamlEditorUtil;

public abstract class MusketPreviewContentAssistProcessor implements IContentAssistProcessor, ICompletionListener {

	private class SuggestionProposal implements ICompletionProposal, ICompletionProposalExtension5, ICompletionProposalExtension7 {
	
		private int offset;
		private int nextSelection;
		private StyledString styledString;
		private String textBefore;
		private Suggestion suggestion;
	
		SuggestionProposal(IDocument document, int offset, Suggestion suggestion) {
			this.offset = offset;
			this.suggestion = suggestion;
	
			String source = document.get();
			textBefore = suggestionsComputer.getTextBefore(source, offset);
		}
	
		@Override
		public void apply(IDocument document) {
			// the proposal shall enter always a space after applyment...
			String proposal = suggestion.text;
			if (isAddingSpaceAtEnd()) {
				proposal += " ";
			}
			int zeroOffset = offset - textBefore.length();
			try {
				document.replace(zeroOffset, textBefore.length(), proposal);
				nextSelection = zeroOffset + proposal.length();
			} catch (BadLocationException e) {
				YamlEditorUtil.logError("Not able to replace by proposal:" + suggestion.text +", zero offset:"+zeroOffset+", textBefore:"+textBefore, e);
			}
	
		}
	
		@Override
		public Point getSelection(IDocument document) {
			Point point = new Point(nextSelection, 0);
			return point;
		}
	
		@Override
		public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
			if (suggestion.title != null && suggestion.description != null) {
				return suggestion.title + '\n' + suggestion.description;
			} else if (suggestion.title != null) {
				return suggestion.title;
			} else {
				return suggestion.description;
			}
		}
		
		@Override
		public String getAdditionalProposalInfo() {
			return null;
		}
	
		@Override
		public String getDisplayString() {
			return suggestion.text;
		}
	
		@Override
		public Image getImage() {
			return null;
		}
	
		@Override
		public IContextInformation getContextInformation() {
			return null;
		}
	
		@Override
		public StyledString getStyledDisplayString(IDocument document, int offset,
				BoldStylerProvider boldStylerProvider) {
			if (styledString != null) {
				return styledString;
			}
			styledString = new StyledString();
			styledString.append(suggestion.text);
			try {
	
				int enteredTextLength = textBefore.length();
				int indexOfTextBefore = suggestion.text.toLowerCase().indexOf(textBefore.toLowerCase());
	
				if (indexOfTextBefore != -1) {
					styledString.setStyle(indexOfTextBefore, enteredTextLength, boldStylerProvider.getBoldStyler());
				}
			} catch (RuntimeException e) {
				YamlEditorUtil.logError("Not able to set styles for proposal:" + suggestion.text, e);
			}
			return styledString;
		}

	}

	private ISuggestionsComputer suggestionsComputer = createSuggestionsComputer();

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		if (document == null) {
			return null;
		}
		String source = document.get();

		Collection<Suggestion> suggestions = suggestionsComputer.calculate(source, offset);

		ICompletionProposal[] result = new ICompletionProposal[suggestions.size()];
		int i = 0;
		for (Suggestion suggestion : suggestions) {
			result[i++] = new SuggestionProposal(document, offset, suggestion);
		}

		return result;
	}

	protected abstract ISuggestionsComputer createSuggestionsComputer();

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public boolean isAddingSpaceAtEnd() {
		return true;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
	
	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
		// Do nothig; That's Yaml editor requirement to implement this
	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {
		// Do nothig; That's Yaml editor requirement to implement this
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
		// Do nothig; That's Yaml editor requirement to implement this
	}
	

}
