package com.onpositive.dside.ui.editors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import org.aml.typesystem.IStatusVisitor;
import org.aml.typesystem.Status;
import org.aml.typesystem.TypeRegistryImpl;
import org.aml.typesystem.values.IArray;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
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
import org.python.pydev.editor.hover.AbstractPyEditorTextHover;
import org.yaml.snakeyaml.nodes.NodeTuple;

import com.onpositive.dside.ast.ASTElement;
import com.onpositive.dside.ast.IHasLocation;
import com.onpositive.dside.ast.TypeRegistryProvider;
import com.onpositive.dside.ast.Universe;
import com.onpositive.dside.dto.introspection.InstrospectedFeature;
import com.onpositive.dside.ui.ExperimentErrorsEditorPart;
import com.onpositive.dside.ui.ExperimentResultsEditorPart;
import com.onpositive.dside.ui.LaunchConfiguration;
import com.onpositive.dside.ui.editors.YamlHyperlinkDetector.FeatureInfo;
import com.onpositive.musket_core.Errors;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentError;
import com.onpositive.musket_core.ExperimentLogs;
import com.onpositive.musket_core.ExperimentResults;
import com.onpositive.musket_core.IExperimentExecutionListener;
import com.onpositive.musket_core.ProjectWrapper;

import de.jcup.yamleditor.YamlEditor;
import de.jcup.yamleditor.YamlSourceViewerConfiguration;

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
	private YamlEditor editor;

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
			editor.setSourceViewerConfiguration(new YamlSourceViewerConfiguration(editor) {

				@Override
				public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
					if (sourceViewer == null)
						return null;

					return new IHyperlinkDetector[] { new URLHyperlinkDetector(),
							new YamlHyperlinkDetector(ExperimentMultiPageEditor.this) };
				}

				protected IContentAssistProcessor createContentAssistProcessor() {
					return new YamlEditorSimpleWordContentAssistProcessor(ExperimentMultiPageEditor.this);
				}

				@Override
				public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
					return new AbstractPyEditorTextHover() {

						@Override
						public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
							FeatureInfo detectFeatures = YamlHyperlinkDetector
									.detectFeatures(ExperimentMultiPageEditor.this, textViewer, hoverRegion);
							if (!detectFeatures.fs.isEmpty()) {
								InstrospectedFeature instrospectedFeature = detectFeatures.fs.get(0);
								if (instrospectedFeature.getDoc() != null
										&& instrospectedFeature.getDoc().length() > 0) {
									return instrospectedFeature.getDoc();
								}
								String source = instrospectedFeature.getSource();
								if (source != null) {
									return source;
								}
							}
							return null;
						}

						@Override
						public boolean isContentTypeSupported(String contentType) {
							return true;
						}
					};
				}

			});
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

	public ProjectWrapper getProject() {
		return formEditor.getProject();
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		try {
			formEditor = new ExperimentOverivewEditorPart(this.editor, experiment);
			this.addPage(0, formEditor, getEditorInput());
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

		form.setText(experiment.toString());
		ArrayList<ExperimentResults> results = experiment.results();
		ArrayList<ExperimentLogs> logs = experiment.logs();
		if (experiment.isCompleted() || results.size() > 0 && logs.size() > 0) {
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

	static class ErrorVisitor implements IStatusVisitor {

		Stack<IHasLocation> stack = new Stack<>();
		protected IFile file;

		public ErrorVisitor(IFile file) {
			super();
			this.file = file;
		}

		class ErrorAndMessage {
			IHasLocation element;
			String message;
			String key;
			boolean onKey;

			public void report() {
				try {
					IMarker marker = file.createMarker("org.eclipse.core.resources.problemmarker");
					int start = 0;
					int end = 0;
					if (element != null) {
						IHasLocation peek = element;
						start = peek.getStartOffset();
						if (peek.getParent() == null) {
							end = start;
						} else {
							end = peek.getEndOffset();
						}
						if (key!=null) {
							NodeTuple findInKey = element.findInKey(key);
							if (findInKey!=null) {
								start=findInKey.getValueNode().getStartMark().getIndex();
								end=findInKey.getValueNode().getEndMark().getIndex();
								if (onKey) {
									start=findInKey.getKeyNode().getStartMark().getIndex();
									end=findInKey.getKeyNode().getEndMark().getIndex();
								}
							}
							
						}
					}

					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					marker.setAttribute(IMarker.CHAR_START, start);
					marker.setAttribute(IMarker.CHAR_END, end);
					marker.setAttribute(IMarker.LOCATION, file.getFullPath().toPortableString());
					marker.setAttribute(IMarker.MESSAGE, message);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			public ErrorAndMessage(IHasLocation element, String message, String key,boolean onKey) {
				super();
				this.element = element;
				this.message = message;
				this.key = key;
				this.onKey=onKey;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + ((element == null) ? 0 : element.hashCode());
				result = prime * result + ((key == null) ? 0 : key.hashCode());
				result = prime * result + ((message == null) ? 0 : message.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				ErrorAndMessage other = (ErrorAndMessage) obj;
				if (!getOuterType().equals(other.getOuterType()))
					return false;
				if (element == null) {
					if (other.element != null)
						return false;
				} else if (!element.equals(other.element))
					return false;
				if (key == null) {
					if (other.key != null)
						return false;
				} else if (!key.equals(other.key))
					return false;
				if (message == null) {
					if (other.message != null)
						return false;
				} else if (!message.equals(other.message))
					return false;
				return true;
			}

			private ErrorVisitor getOuterType() {
				return ErrorVisitor.this;
			}

		}

		public HashSet<ErrorAndMessage> messages = new HashSet<>();

		@Override
		public void startVisiting(Status st) {
			try {
				
				Object source = st.getSource();
				if (source instanceof IHasLocation) {
					stack.push((IHasLocation) source);
				}
				

				if (stack.size() > 0) {
					if (!st.isOk()) {
					IHasLocation peek = stack.peek();
					ErrorAndMessage e = new ErrorAndMessage(peek, st.getMessage(), st.getKey(),st.isOnKey());
					for (ErrorAndMessage m : new ArrayList<>(this.messages)) {
						if (m.message.equals(st.getMessage())) {
							IHasLocation z = peek;
							while (z != null) {
								if (z.equals(m.element)) {
									this.messages.remove(m);
								}
								z = z.getParent();
							}
						}
					}
					
						messages.add(e);
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void endVisiting(Status st) {
			Object source = st.getSource();
			if (source instanceof IHasLocation) {
				stack.pop();
				if (stack.isEmpty()) {
					this.messages.forEach(v -> v.report());
				}
			}
		}

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
		Universe registry = TypeRegistryProvider.getRegistry("basicConfig");

		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			FileEditorInput fl = (FileEditorInput) editorInput;
			IFile file = fl.getFile();
			try {
				IMarker[] findMarkers = file.findMarkers("org.eclipse.core.resources.problemmarker", true, 1);
				for (IMarker m : findMarkers) {
					m.delete();
				}
				String string = editor.getDocument().get();
				Status validate = registry.validate(string, getProject().getDetails());
				ErrorVisitor st = new ErrorVisitor(file);
				validate.visitErrors(st);
				System.out.println(st);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
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
