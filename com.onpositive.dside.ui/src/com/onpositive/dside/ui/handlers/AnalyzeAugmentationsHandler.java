package com.onpositive.dside.ui.handlers;

import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.onpositive.dside.tasks.analize.AnalyzeAugmentationsDialogModel;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.WorkbenchUIUtils;
import com.onpositive.dside.ui.editors.ExperimentMultiPageEditor;
import com.onpositive.dside.ui.editors.ObjectEditorInput;
import com.onpositive.dside.ui.editors.preview.augmentation.AugmentationPreviewDelegate;
import com.onpositive.dside.ui.editors.preview.augmentation.AugmentationsPreviewEditorPart;
import com.onpositive.musket_core.Experiment;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.yamledit.io.YamlIO;

import de.jcup.yamleditor.YamlEditor;

public class AnalyzeAugmentationsHandler extends AbstractExperimentEditorHandler {

	@Override
	protected void executeOnExperimentEditor(ExperimentMultiPageEditor experimentEditor) {
		YamlEditor editor = experimentEditor.getAdapter(YamlEditor.class);
		if (editor != null) {
			Experiment experiment = experimentEditor.getExperiment();
			String yamlText = editor.getDocument().get();
			Object loaded = null;
			try {
				loaded = YamlIO.load(yamlText);
			} catch (Exception e1) {
				// Best effort
			}
			if (!(loaded instanceof Map<?,?>)) {
				MessageDialog.openWarning(editor.getSite().getShell(), "Invalid experiment configuration", "Experiment configuration Yaml syntx is invalid, can't analyze augmentations");
				return;
			}
			Map<?, ?> yamlMap = (Map<?, ?>) loaded;
			Object augmentation = yamlMap.get("augmentation");
			if (!(augmentation instanceof Map<?,?>) || ((Map<?,?>) augmentation).isEmpty()) {
				MessageDialog.openWarning(editor.getSite().getShell(), "No augmentation specified", "No augmentations specified in given config. It should have non-empty 'augmentations:' declaration.");
				return;				
			}
			AnalyzeAugmentationsDialogModel initial = new AnalyzeAugmentationsDialogModel(experiment);
			boolean proceed = WidgetRegistry.createObject(initial);
			if (proceed) {
				if (!(editor.getEditorInput() instanceof IFileEditorInput)) {
					MessageDialog.openWarning(editor.getSite().getShell(), "Input not supported", "This input type (" + editor.getEditorInput().getName() + ":"  + editor.getEditorInput().getClass().getSimpleName() + ") is not supported by this action.");
					return;
				}
				IFileEditorInput fileEditorInput = (IFileEditorInput) editor.getEditorInput();
				AugmentationPreviewDelegate previewDelegate = new AugmentationPreviewDelegate(experiment, yamlText, fileEditorInput.getFile(), initial.getDataset(), initial.isDebug());
				try {
					WorkbenchUIUtils.getActivePage().openEditor(new ObjectEditorInput(previewDelegate), AugmentationsPreviewEditorPart.ID);
				} catch (PartInitException e) {
					DSIDEUIPlugin.log(e);
				}
				editor.getSite().getPage().addPartListener(new IPartListener() {
					
					@Override
					public void partOpened(IWorkbenchPart part) {
						//Do nothing
					}
					
					@Override
					public void partDeactivated(IWorkbenchPart part) {
						//Do nothing
					}
					
					@Override
					public void partClosed(IWorkbenchPart part) {
						previewDelegate.close();
					}
					
					@Override
					public void partBroughtToTop(IWorkbenchPart part) {
						//Do nothing
					}
					
					@Override
					public void partActivated(IWorkbenchPart part) {
						//Do nothing
					}
				});
			}
			
		}
	}

}
