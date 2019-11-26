package com.onpositive.dside.ui.editors.preview;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.onpositive.dside.tasks.DebounceExecutor;
import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.editors.DeprecatedControl;
import com.onpositive.dside.ui.editors.ObjectEditorInput;

import de.jcup.yamleditor.YamlEditor;

public abstract class MusketPreviewEditorPart extends YamlEditor {
	
	private IPreviewEditDelegate previewDelegate;
	
	private DebounceExecutor debounceExecutor = new DebounceExecutor();
	
	@Override
	public void createPartControl(Composite parent) {
		if (previewDelegate == null) {
			new DeprecatedControl(parent);
		} else {
			SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
			super.createPartControl(sashForm);
			createPreviewControl(sashForm);
			updatePreview();
			getSourceViewer().addTextListener((event) -> {
				debounceExecutor.debounce(1000, () -> {
					updatePreview();
				});
			});
		}
	}

	protected abstract void createPreviewControl(Composite parent);

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		if (input instanceof ObjectEditorInput) {
			if (((ObjectEditorInput) input).getObject() != null) {	
				previewDelegate = (IPreviewEditDelegate) ((ObjectEditorInput) input).getObject();
				try {
					File tempFile = File.createTempFile("preview", ".yaml");
					FileUtils.writeStringToFile(tempFile, previewDelegate.getInitialText());
					super.doSetInput(new FileStoreEditorInput(EFS.getStore(tempFile.toURI())));
				} catch (IOException e) {
					DSIDEUIPlugin.log(e);
				}
			} else {
				previewDelegate = null;
			}
		} else { 
			super.doSetInput(input);
		}
	}
	
	@Override
	public String getPartName() {
		return "Augmentations preview";
	}
	
	public void updatePreview() {
		previewDelegate.setText(getDocument().get());
		previewDelegate.refreshPreview(results -> {
			doRefreshPreview(results);
		},
		exception -> {
			handleError(exception);
		});
	}
	
	@Override
	protected boolean containsSavedState(IMemento memento) {
		return false; //Avoid NPE while trying to restore
	}

	protected void handleError(Throwable exception) {
		ErrorDialog.openError(getSite().getShell(), "Error refreshing preview", "Exception occured while processing request", new Status(IStatus.ERROR,DSIDEUIPlugin.PLUGIN_ID, "Exception", exception));
	}

	protected abstract void doRefreshPreview(IAnalizeResults results);

}
