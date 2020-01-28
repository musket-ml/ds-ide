package com.onpositive.dside.ui.editors.preview;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.onpositive.dside.tasks.DebounceExecutor;
import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.editors.DeprecatedControl;
import com.onpositive.dside.ui.editors.IExperimentConfigEditor;
import com.onpositive.dside.ui.editors.ObjectEditorInput;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ProjectManager;
import com.onpositive.musket_core.ProjectWrapper;
import com.onpositive.yamledit.ast.TypeRegistryProvider;
import com.onpositive.yamledit.ast.Universe;

import de.jcup.yamleditor.YamlEditor;

public abstract class MusketPreviewEditorPart extends YamlEditor implements IExperimentConfigEditor {
	
	private static final String EDITOR_CONTEXT = "com.onpositive.dside.ui.editors.preview.context";

	private IExperimentPreviewEditDelegate previewDelegate;
	
	private DebounceExecutor debounceExecutor = new DebounceExecutor();
	
	private Experiment experiment;
	
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
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor); //We leave default behaviour to avoid inconsistency in editor/file state
		previewDelegate.save();
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		if (input instanceof ObjectEditorInput) {
			if (((ObjectEditorInput) input).getObject() != null) {	
				previewDelegate = (IExperimentPreviewEditDelegate) ((ObjectEditorInput) input).getObject();
				experiment = previewDelegate.getExperiment();
				try {
					File tempFile = File.createTempFile("preview", ".yaml");
					FileUtils.writeStringToFile(tempFile, previewDelegate.getText());
					super.doSetInput(createEditorInput(tempFile));
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

	private FileStoreEditorInput createEditorInput(File tempFile) throws CoreException {
		return new FileStoreEditorInput(EFS.getStore(tempFile.toURI())) {
			@Override
			public String getName() {
				return "preview for " + getExperiment().toString();
			}
		};
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
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(getContext());
		setRulerContextMenuId(getContext());
	}

	protected String getContext() {
		return EDITOR_CONTEXT;
	}

	protected void activateYamlEditorContext() {
		IContextService contextService = getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext(getContext());
		}
	}
	
	@Override
	protected boolean containsSavedState(IMemento memento) {
		return false; //Avoid NPE while trying to restore
	}

	protected void handleError(Throwable exception) {
//		Display.getDefault().asyncExec(() -> {
//			ErrorDialog.openError(getSite().getShell(), "Error refreshing preview", "Exception occured while processing request", new Status(IStatus.ERROR,DSIDEUIPlugin.PLUGIN_ID, "Exception", exception));
//		});
	}

	protected abstract void doRefreshPreview(IAnalizeResults results);
	
	@Override
	public ProjectWrapper getProject() {
		return ProjectManager.getInstance().getProject(experiment);
	}

	@Override
	public Universe getRegistry() {
		Universe registry = TypeRegistryProvider.getRegistry("basicConfig");
		registry.setProjectContext(getProject().getProjectContext());
		return registry;
	}
	
	@Override
	public Experiment getExperiment() {
		return experiment;
	}

}
