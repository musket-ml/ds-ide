package com.onpositive.dside.ui.editors.preview.augmentation;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

import com.onpositive.dside.ui.editors.IExperimentConfigEditor;
import com.onpositive.dside.ui.editors.preview.ISuggestionsComputer;
import com.onpositive.dside.ui.editors.preview.MusketPreviewContentAssistProcessor;
import com.onpositive.dside.ui.editors.preview.MusketPreviewEditorPart;

import de.jcup.yamleditor.YamlSourceViewerConfiguration;

public class AugmentationsSourceViewerConfiguration extends YamlSourceViewerConfiguration {

	public AugmentationsSourceViewerConfiguration(MusketPreviewEditorPart editor) {
		super(editor);
	}
	
	protected IContentAssistProcessor createContentAssistProcessor(IAdaptable adaptable) {
		return new MusketPreviewContentAssistProcessor() {
			
			@Override
			protected ISuggestionsComputer createSuggestionsComputer() {
				return new IntrospectedAugmentationsSuggestionsComputer((IExperimentConfigEditor) adaptable);
			}
		};
//		return new ExperimentConfigContentAssistProcessor((IExperimentConfigEditor) adaptable);
	}

}
