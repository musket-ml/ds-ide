package com.onpositive.dside.ui.editors;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.dside.ui.ExperimentErrorsEditorPart;
import com.onpositive.dside.ui.ExperimentResultsEditorPart;
import com.onpositive.dside.ui.LaunchConfiguration;
import com.onpositive.musket_core.Errors;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentError;
import com.onpositive.musket_core.ExperimentLogs;
import com.onpositive.musket_core.ExperimentResults;
import com.onpositive.musket_core.IExperimentExecutionListener;

import de.jcup.yamleditor.YamlEditor;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class ExperimentMultiPageEditor extends SharedHeaderFormEditor implements IResourceChangeListener {

	/** The text editor used in page 0. */
	private TextEditor editor;


	public ExperimentMultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createPage0() {
		try {
			editor = new YamlEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	IExperimentExecutionListener listener = new IExperimentExecutionListener() {

		@Override
		public void complete(Experiment e) {
			if (experiment != null) {
				if (e.getPath().equals(experiment.getPath())) {
					updatePages();
				}
			}
		}
	};

	private Experiment experiment;

	private ExperimentOverivewEditorPart formEditor;

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		try {
			formEditor = new ExperimentOverivewEditorPart(this.editor,experiment);
			this.addPage(0,formEditor, getEditorInput());
			setPageText(0, "Overview");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updatePages();
		LaunchConfiguration.addListener(listener);
	}

	private void updatePages() {
		for (int i = 2; i < this.getPageCount(); i++) {
			this.removePage(i);
		}
		

		IFileEditorInput e = (IFileEditorInput) getEditorInput();
		IPath location = e.getFile().getParent().getLocation();
		this.setPartName(experiment.toString());
		ScrolledForm form = this.getHeaderForm().getForm();
		Action action = new Action() {
			@Override
			public void run() {
				System.out.println("Launch");
			}
		};
		action.setText("Launch Experiment");
		action.setImageDescriptor(DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_ACT_RUN));
		form.getToolBarManager().add(action);
		form.getToolBarManager().update(true);
		form.setText(experiment.toString());
		ArrayList<ExperimentResults> results = experiment.results();
		ArrayList<ExperimentLogs> logs = experiment.logs();
		if (experiment.isCompleted() || results.size()>0 &&logs.size()>0) {
			ExperimentResultsEditorPart ps = new ExperimentResultsEditorPart(experiment);
			try {
				int pageCount = this.getPageCount();
				this.addPage(ps, e);
				setPageText(pageCount, "Results and Logs");

			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Errors errors = experiment.getErrors();
		ArrayList<ExperimentError> errors2 = errors.getErrors();
		if (!errors2.isEmpty()) {
			try {
				int pageCount = this.getPageCount();
				ExperimentErrorsEditorPart rp = new ExperimentErrorsEditorPart(errors);
				this.addPage(rp, e);
				setPageText(pageCount, "Errors");
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	protected void addPages() {

	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors. Subclasses
	 * may extend.
	 */
	public void dispose() {
		LaunchConfiguration.removeListener(listener);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		if (formEditor.isDirty()) {
			formEditor.updateText();
		}
		getEditor(0).doSave(monitor);
		getEditor(1).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the text
	 * for page 0's tab, and updates this multi-page editor's input to correspond to
	 * the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method checks
	 * that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
		IFileEditorInput e = (IFileEditorInput) getEditorInput();
		IPath location = e.getFile().getParent().getLocation();
		experiment = new Experiment(location.toPortableString());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 0) {
			formEditor.updateFromText();
		}
		if (newPageIndex == 1) {
			formEditor.updateText();
		}
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) editor.getEditorInput()).getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

}
