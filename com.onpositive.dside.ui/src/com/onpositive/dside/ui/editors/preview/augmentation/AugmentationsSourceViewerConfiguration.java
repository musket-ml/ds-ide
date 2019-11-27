package com.onpositive.dside.ui.editors.preview.augmentation;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

import com.onpositive.dside.ui.editors.preview.ISuggestionsComputer;
import com.onpositive.dside.ui.editors.preview.MusketPreviewContentAssistProcessor;

import de.jcup.yamleditor.YamlSourceViewerConfiguration;

public class AugmentationsSourceViewerConfiguration extends YamlSourceViewerConfiguration {

	public AugmentationsSourceViewerConfiguration(IAdaptable adaptable) {
		super(adaptable);
	}
	
	protected IContentAssistProcessor createContentAssistProcessor() {
		return new MusketPreviewContentAssistProcessor() {
			
			@Override
			protected ISuggestionsComputer createSuggestionsComputer() {
				return new AugmentationsEditorSuggestionsComputer();
			}
		};
	}

}
