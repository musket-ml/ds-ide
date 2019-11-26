package com.onpositive.dside.ui.editors.preview;

import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.core.resources.IFile;

import com.onpositive.dside.dto.ImageDataSetAugmentRequest;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.musket_core.Experiment;
import com.onpositive.yamledit.io.YamlIO;

public class AugmentationPreviewDelegate implements IPreviewEditDelegate {
	
	private String initialText;
	private Experiment experiment;
	private IFile inputFile;
	private int offset;
	private String selectedDataset;
	private String currentText;
	private boolean debug;

	public AugmentationPreviewDelegate(Experiment experiment, String yamlText, IFile inputFile, String selectedDataset, boolean debug) {
		this.experiment = experiment;
		this.debug = debug;
		this.currentText = this.initialText = extractInitialText(yamlText);
		if (this.initialText == null) {
			throw new IllegalArgumentException("yamlText should be valid config.yaml with 'augmentations:' section in it");
		}
		this.inputFile = inputFile;
		this.offset = yamlText.indexOf(initialText);
		this.selectedDataset = selectedDataset;
	}

	private String extractInitialText(String yamlText) {
		int idx = yamlText.indexOf("augmentation:");
		if (idx > 0) {
			String[] rest = yamlText.substring(idx).split("\n"); 
			int basicIndent = countWhitespace(rest[0]);
			int lastLine = 1;
			StringBuilder builder = new StringBuilder();
			while (lastLine < rest.length && countWhitespace(rest[lastLine]) > basicIndent) { 
				builder.append(rest[lastLine++]);
				builder.append('\n');
			}
			return builder.toString();
		}
		return null;
	}

	private int countWhitespace(String line) {
		int i = 0;
		while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
			i++;
		}
		return i;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public String getInitialText() {
		return initialText;
	}
	
	@Override
	public String getText() {
		return currentText;
	}

	@Override
	public void setText(String text) throws IllegalArgumentException {
		currentText = text;
	}

	@Override
	public void refreshPreview(Consumer<IAnalizeResults> onSuccess, Consumer<Throwable> onFail) {
		Object loaded = YamlIO.load(currentText);
		if (!(loaded instanceof Map<?,?>) || ((Map<?,?>) loaded).isEmpty()) {
			return; //Just ignore for now
//			onFail.accept(new IllegalArgumentException("Argument should be valid non-empty Yaml property list"));
		}
		ImageDataSetAugmentRequest data = new ImageDataSetAugmentRequest(experiment.createModelSpec(),
				selectedDataset, experiment.getPath().toOSString(), currentText);


		GateWayRelatedTask gateWayRelatedTask = new GateWayRelatedTask(
				inputFile.getProject(), new IGateWayServerTaskDelegate() {

					@Override
					public void terminated() {
						// TODO Auto-generated method stub

					}

					@Override
					public void started(GateWayRelatedTask task) {

						task.perform(data, IAnalizeResults.class, (r) -> {
							onSuccess.accept(r);
						}, (e) -> {
							onFail.accept(e);
						});
					}
				});
		gateWayRelatedTask.setDebug(debug); 
		TaskManager.perform(gateWayRelatedTask);

	}

}
