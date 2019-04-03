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
package com.onpositive.dside.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.aml.typesystem.AbstractType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.BoldStylerProvider;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension7;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.dside.ast.CompletionContext;
import com.onpositive.dside.ast.TypeRegistryProvider;
import com.onpositive.dside.ast.Universe.CompletionSuggestions;
import com.onpositive.dside.dto.introspection.InstrospectedFeature;
import com.onpositive.semantic.model.api.property.IProperty;

import de.jcup.yamleditor.YamlEditorUtil;

public class YamlEditorSimpleWordContentAssistProcessor implements IContentAssistProcessor, ICompletionListener {

	private String errorMessage;
	ExperimentMultiPageEditor editor;

	public YamlEditorSimpleWordContentAssistProcessor(ExperimentMultiPageEditor editor) {
		super();
		this.editor = editor;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		if (document == null) {
			return null;
		}

		CompletionContext completionContext = CompletionContextBuilder.build(document, offset);
		ArrayList<String> seq = completionContext.getSeq();

		ArrayList<InstrospectedFeature> features = editor.getProject().getDetails().getFeatures();
		CompletionSuggestions find = TypeRegistryProvider.getRegistry("basicConfig").find(completionContext,
				editor.getProject().getDetails());
		LinkedHashSet<String> strs = new LinkedHashSet<>();
		ArrayList<ICompletionProposal> ps = new ArrayList<>();
		if (find != null) {
			if (find.ts != null) {				
				for (org.aml.typesystem.beans.IProperty v : find.ts.getType().toPropertiesView().properties()) {
					if (!strs.add(v.id().toLowerCase())) {
						continue;
					}
					if (find.ts.findInKey(v.id())!=null) {
						continue;
					}
					if (v.id().toLowerCase().startsWith(completionContext.completionStart.toLowerCase())) {
					ps.add(new SimpleWordProposal(document, offset,
							v.id()+":",
							completionContext.completionStart, null));
					}
				}
			} else {
				for (AbstractType v : find.values) {
					if (!strs.add(v.name().toLowerCase())) {
						continue;
					}
					if (v.name().toLowerCase().startsWith(completionContext.completionStart.toLowerCase())) {
					ps.add(new SimpleWordProposal(document, offset,
							v.name().substring(0, 1).toLowerCase() + v.name().substring(1),
							completionContext.completionStart, null));
					}
				}
			}
		} else {
			LinkedHashSet<String> filter = null;
			if (!seq.isEmpty()) {
				String string = CompletionContext.getContexts().get(seq.get(0));
				if (string != null) {
					String[] split = string.split(",");
					filter = new LinkedHashSet<>(Arrays.asList(split));
				}
			}

			for (InstrospectedFeature f : features) {
				if (!strs.add(f.getName())) {
					continue;
				}
				if (filter != null) {
					if (!filter.contains(f.getKind())) {
						continue;
					}
				}
				if (f.getName().toLowerCase().startsWith(completionContext.completionStart.toLowerCase())) {
					ps.add(new SimpleWordProposal(document, offset,
							f.getName().substring(0, 1).toLowerCase() + f.getName().substring(1),
							completionContext.completionStart, f));
				}
			}
		}
//		Set<String> words = simpleWordCompletion.calculate(source, offset);
//
//		ICompletionProposal[] result = new ICompletionProposal[words.size()];
//		int i = 0;
//		for (String word : words) {
//			result[i++] = new SimpleWordProposal(document, offset, word);
//		}
		Collections.sort(ps, (x,y)->{
			return x.getDisplayString().compareTo(y.getDisplayString());
		});
		return ps.toArray(new ICompletionProposal[ps.size()]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	private class SimpleWordProposal implements ICompletionProposal, ICompletionProposalExtension7 {

		private int offset;
		private String word;
		private int nextSelection;
		private StyledString styledString;
		private String textBefore;
		private InstrospectedFeature feature;

		SimpleWordProposal(IDocument document, int offset, String word, String start, InstrospectedFeature feature) {
			this.offset = offset;
			this.word = word;

			String source = document.get();
			this.textBefore = start;
			this.feature = feature;
			// textBefore = simpleWordCompletion.getTextbefore(source, offset);
		}

		@Override
		public void apply(IDocument document) {
			// the proposal shall enter always a space after applyment...
			String proposal = word;
			if (isAddingSpaceAtEnd()) {
				proposal += " ";
			}
			int zeroOffset = offset - textBefore.length();
			try {
				document.replace(zeroOffset, textBefore.length(), proposal);
				nextSelection = zeroOffset + proposal.length();
			} catch (BadLocationException e) {
				YamlEditorUtil.logError("Not able to replace by proposal:" + word + ", zero offset:" + zeroOffset
						+ ", textBefore:" + textBefore, e);
			}

		}

		@Override
		public Point getSelection(IDocument document) {
			Point point = new Point(nextSelection, 0);
			return point;
		}

		@Override
		public String getAdditionalProposalInfo() {
			if (feature == null) {
				return "";
			}
			if (feature.getDoc() != null && feature.getDoc().length() > 0) {
				String doc = feature.getDoc();

				return doc;
			}
			return feature.getSource();
		}

		@Override
		public String getDisplayString() {
			return word;
		}

		@Override
		public Image getImage() {
			return SWTImageManager.getImage("analize_data");
		}

		@Override
		public IContextInformation getContextInformation() {
			return new IContextInformation() {

				@Override
				public String getInformationDisplayString() {
					if (feature.getDoc() != null && feature.getDoc().length() > 0) {
						return feature.getDoc();
					}
					return feature.getSource();
				}

				@Override
				public Image getImage() {
					return SWTImageManager.getImage("analize_data");
				}

				@Override
				public String getContextDisplayString() {
					return "element";
				}
			};
		}

		@Override
		public StyledString getStyledDisplayString(IDocument document, int offset,
				BoldStylerProvider boldStylerProvider) {
			if (styledString != null) {
				return styledString;
			}
			styledString = new StyledString();
			styledString.append(word);
			try {

				int enteredTextLength = textBefore.length();
				int indexOfTextBefore = word.toLowerCase().indexOf(textBefore.toLowerCase());

				if (indexOfTextBefore != -1) {
					styledString.setStyle(indexOfTextBefore, enteredTextLength, boldStylerProvider.getBoldStyler());
				}
			} catch (RuntimeException e) {
				YamlEditorUtil.logError("Not able to set styles for proposal:" + word, e);
			}
			return styledString;
		}

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
		return errorMessage;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public ICompletionListener getCompletionListener() {
		return this;
	}

	/* completion listener parts: */

	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {

	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {

	}

}
