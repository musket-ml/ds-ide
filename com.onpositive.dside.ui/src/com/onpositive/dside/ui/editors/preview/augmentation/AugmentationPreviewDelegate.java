package com.onpositive.dside.ui.editors.preview.augmentation;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.onpositive.dside.dto.ImageDataSetAugmentRequest;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.WorkbenchUIUtils;
import com.onpositive.dside.ui.editors.preview.IExperimentPreviewEditDelegate;
import com.onpositive.musket_core.Experiment;
import com.onpositive.yamledit.io.YamlIO;

public class AugmentationPreviewDelegate implements IExperimentPreviewEditDelegate {
	
	private static final String AUGMENTATION_PREFFIX = "augmentation:";
	private String initialText;
	private Experiment experiment;
	private IFile inputFile;
	private int offset;
	private String selectedDataset;
	private String currentText;
	private boolean debug;
	private GateWayRelatedTask gateWayRelatedTask;

	
	public AugmentationPreviewDelegate(Experiment experiment, String yamlText, IFile inputFile, String selectedDataset, boolean debug) {
		this.experiment = experiment;
		this.debug = debug;
		this.initialText = extractInitialText(yamlText);
		this.currentText = unIndent(initialText);
		if (this.initialText == null) {
			throw new IllegalArgumentException("yamlText should be valid config.yaml with 'augmentations:' section in it");
		}
		this.inputFile = inputFile;
		this.offset = yamlText.indexOf(initialText);
		this.selectedDataset = selectedDataset;
	}

	private String unIndent(String indented) {
		String[] parts = StringUtils.split(indented,'\n');
		String whitespace = getWhitespace(parts[0]);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].startsWith(whitespace)) {
				builder.append(parts[i].substring(whitespace.length()));
			} else {
				builder.append(ltrim(parts[i]));
			}
			if (i < parts.length - 1) {
				builder.append('\n');
			}
		}
		return builder.toString();
	}
	
	private String reIndent(String unIndented) {
		String[] parts = unIndented.split("\\R");
		StringBuilder builder = new StringBuilder();
		String indent = getWhitespace(initialText);
		for (int i = 0; i < parts.length; i++) {
			builder.append(indent);
			builder.append(parts[i]);
			builder.append('\n');
		}
		return builder.toString();
	}

	private String extractInitialText(String yamlText) {
		int idx = yamlText.indexOf(AUGMENTATION_PREFFIX);
		if (idx > 0) {
			String[] rest = yamlText.substring(idx).split("\n"); 
			int basicIndent = getWhitespace(rest[0]).length();
			int lastLine = 1;
			StringBuilder builder = new StringBuilder();
			while (lastLine < rest.length && getWhitespace(rest[lastLine]).length() > basicIndent) { 
				builder.append(rest[lastLine++]);
				builder.append('\n');
			}
			return builder.toString();
		}
		return null;
	}

	private String getWhitespace(String line) {
		int i = 0;
		while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
			i++;
		}
		return line.substring(0,i);
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

		callTask(onSuccess, onFail, data);
	}

	private synchronized void callTask(Consumer<IAnalizeResults> onSuccess, Consumer<Throwable> onFail,
			ImageDataSetAugmentRequest data) {
		if (gateWayRelatedTask == null) {
			gateWayRelatedTask = new GateWayRelatedTask(
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
		} else {
			gateWayRelatedTask.perform(data, IAnalizeResults.class, (r) -> {
				onSuccess.accept(r);
			}, (e) -> {
				onFail.accept(e);
			});
		}
	}

	@Override
	public void save() {
		IEditorPart editor = WorkbenchUIUtils.getEditorForFile(inputFile);
		
		IDocument origDoc = getCurrentContent((ITextEditor) editor, inputFile);
		String origText = StringUtils.defaultString(extractInitialText(origDoc.get()));
		if (!origText.trim().equals(initialText.trim())) {
			boolean doSave = MessageDialog.openQuestion(WorkbenchUIUtils.getActiveShell(), "Original file changed", "File " + inputFile.getName() + " has changed in the meanwhile. Save anyway?"); 
			if (!doSave) {
				return;
			}
		}
		int replaceIdx = origText.length() > 0 ? origDoc.get().indexOf(origText) : origDoc.getLength() - 1;
		int replaceLen = origText.length();
		if (replaceIdx == -1) {
			origDoc.set(origDoc.get() + "\n" + AUGMENTATION_PREFFIX);
			replaceIdx = origDoc.getLength() - 1;
		}
		try {
			origDoc.replace(replaceIdx, replaceLen, reIndent(currentText));
		} catch (BadLocationException e) {
			DSIDEUIPlugin.log(e);
		}
		
		if (editor == null) {
			try {
				inputFile.setContents(new ByteArrayInputStream(origDoc.get().getBytes(Charset.forName("UTF-8"))), IResource.FORCE | IResource.KEEP_HISTORY, new NullProgressMonitor());
			} catch (CoreException e) {
				DSIDEUIPlugin.log(e);
			}
		}
	}

	protected IDocument getCurrentContent(ITextEditor editor, IFile file) {
		IDocument doc = null;
		if (editor instanceof ITextEditor) {
			doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		}
		if (doc != null) {
			return doc;
		}
		try {
			String result = IOUtils.toString(file.getContents(true), StandardCharsets.UTF_8.name());
			doc = new Document(result);
		} catch (Exception e) {
			DSIDEUIPlugin.log(e);		
		}
		return doc;
	}
	
	public static String ltrim(String s) {
	    int i = 0;
	    while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
	        i++;
	    }
	    return s.substring(i);
	}

	@Override
	public Experiment getExperiment() {
		return experiment;
	}

	public void close() {
		if (gateWayRelatedTask != null) {
			gateWayRelatedTask.terminate();
		}
	}

}
