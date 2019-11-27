package com.onpositive.dside.ui.handlers;

import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.dside.tasks.analize.AnalyzeAugmentationsDialogModel;
import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.WorkbenchUIUtils;
import com.onpositive.dside.ui.editors.AugmentationOverviewEditorPart;
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
			}
			
		}
	}
//	@Override
//	protected void executeOnExperimentEditor(ExperimentMultiPageEditor experimentEditor) {
//		YamlEditor editor = experimentEditor.getAdapter(YamlEditor.class);
//		if (editor != null) {
//			Experiment experiment = experimentEditor.getExperiment();
//			String yamlText = editor.getDocument().get();
//			Object loaded = YamlIO.load(yamlText);
//			if (loaded instanceof Map<?,?>) {
//				Map<?, ?> yamlMap = (Map<?, ?>) loaded;
//				Object augmentation = yamlMap.get("augmentation");
//				if (augmentation instanceof Map<?,?> && !((Map<?,?>) augmentation).isEmpty()) {
//					AnalyzeAugmentationsDialogModel initial = new AnalyzeAugmentationsDialogModel(experiment);
//					boolean proceed = WidgetRegistry.createObject(initial);
//					if (proceed) {
//						ImageDataSetAugmentRequest data = new ImageDataSetAugmentRequest(experiment.createModelSpec(),
//								initial.getDataset(), experiment.getPath().toOSString(), YamlIO.dump(augmentation));
//						
//						FileEditorInput editorInput = (FileEditorInput) editor.getEditorInput();
//						
//						GateWayRelatedTask gateWayRelatedTask = new GateWayRelatedTask(
//								editorInput.getFile().getProject(), new IGateWayServerTaskDelegate() {
//									
//									@Override
//									public void terminated() {
//										// TODO Auto-generated method stub
//										
//									}
//									
//									@Override
//									public void started(GateWayRelatedTask task) {
//										
//										task.perform(data, IAnalizeResults.class, (r) -> {
//											display(r);
//										}, (e) -> {
//											onError(e);
//										});
//									}
//								});
//						gateWayRelatedTask.setDebug(initial.isDebug()); 
//						TaskManager.perform(gateWayRelatedTask);
//					}
//				}
//			}
//			
//		}
//	}

	protected void onError(Throwable e) {
		if (e instanceof Exception) {
			DSIDEUIPlugin.log((Exception) e);
		} else {
			e.printStackTrace();
		}
	}

	protected void display(IAnalizeResults results) {
		ObjectEditorInput er=new ObjectEditorInput(results);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
					er, AugmentationOverviewEditorPart.ID);
		} catch (PartInitException e1) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e1.getMessage());
		}
		
		
	}

}
