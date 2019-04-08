package com.onpositive.dside.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.aml.typesystem.Status;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.python.pydev.editor.hover.AbstractPyEditorTextHover;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.dside.ast.ASTElement;
import com.onpositive.dside.ast.TypeRegistryProvider;
import com.onpositive.dside.ast.Universe;
import com.onpositive.dside.dto.introspection.InstrospectedFeature;
import com.onpositive.dside.ui.ExperimentErrorsEditorPart;
import com.onpositive.dside.ui.ExperimentResultsEditorPart;
import com.onpositive.dside.ui.LaunchConfiguration;
import com.onpositive.dside.ui.editors.YamlHyperlinkDetector.FeatureInfo;
import com.onpositive.dside.ui.editors.outline.OutlineContentProvider;
import com.onpositive.musket_core.Errors;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentError;
import com.onpositive.musket_core.ExperimentLogs;
import com.onpositive.musket_core.ExperimentResults;
import com.onpositive.musket_core.IExperimentExecutionListener;
import com.onpositive.musket_core.ProjectWrapper;
import com.onpositive.semantic.model.ui.property.editors.SeparatorElement;

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
				public IReconciler getReconciler(ISourceViewer sourceViewer) {
					// TODO Auto-generated method stub
					return new MonoReconciler(new IReconcilingStrategy() {
						
						

						@Override
						public void setDocument(IDocument document) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
							reconcile(null);
						}

						@Override
						public void reconcile(IRegion partition) {
							root=null;
							try {
							getRoot();
							if (page!=null) {
								Display.getDefault().asyncExec(new Runnable() {
									
									@Override
									public void run() {
										((ExperimentOutline) page).refresh();
									}
								});
							}
							}catch (Exception e) {
								e.printStackTrace();
								// TODO: handle exception
							}
						}
						
					},false);					
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
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		
		if (IContentOutlinePage.class.equals(adapter)) {
			return (T) getOutlinePage();
		}
		
		if (IFile.class.equals(adapter)) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput feditorInput = (IFileEditorInput) input;
				return (T) feditorInput.getFile();
			}
			return null;
		}
//		if (ISourceViewer.class.equals(adapter)) {
//			return (T) getSourceViewer();
//		}
//		if (StatusMessageSupport.class.equals(adapter)) {
//			return (T) this;
//		}
//		if (ITreeContentProvider.class.equals(adapter) || YamlEditorTreeContentProvider.class.equals(adapter)) {
//			if (outlinePage == null) {
//				return null;
//			}
//			return (T) outlinePage.getContentProvider();
//		}
		return super.getAdapter(adapter);
	}
	IContentOutlinePage page;

	private IContentOutlinePage getOutlinePage() {
		if (page!=null) {
			return page;
		}
		page=new ExperimentOutline(this);
		return page;
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		formEditor = new ExperimentOverivewEditorPart(this.editor, experiment,this);
//			this.addPage(0, formEditor, getEditorInput());
//			setPageText(0, "Overview");
		try {
			formEditor.init(getEditorSite(), getEditorInput());
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		updatePages();
		LaunchConfiguration.addListener(listener);
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_OUTLINE);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Composite head=getHeaderForm().getForm().getForm().getHead();
		Composite headClient=new Composite(head, SWT.NONE);
		
		headClient.setLayout(new RowLayout ());
		for (EditorTask task : EditorTasks.getTasks()) {
			represent(headClient, task);
		}
		Label label = new Label(headClient, SWT.SEPARATOR|SWT.VERTICAL);
		RowData layoutData = new RowData(3, 18);		
		label.setLayoutData(layoutData);
		List<InstrospectedFeature> tasks = getProject().getTasks();
		tasks.forEach(r -> {
			represent(headClient, new EditorTasks.UserTask(r));
		});
		getHeaderForm().getForm().getForm().setHeadClient(headClient);
		validate();
	}

	private void represent(Composite headClient, EditorTask task) {
		ImageHyperlink createImageHyperlink = getHeaderForm().getToolkit().createImageHyperlink(headClient, SWT.NONE);
		createImageHyperlink.setText(task.name);
		createImageHyperlink.setImage(SWTImageManager.getImage(task.image));
		createImageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
			 task.perform(formEditor, experiment);
			}
		});
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
		formEditor.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);		
		super.dispose();
	}

	protected ASTElement root;
	
	public ASTElement getRoot() {
		if (this.root!=null) {
			return root;
		}
		Universe registry = TypeRegistryProvider.getRegistry("basicConfig");
		ASTElement buildRoot = registry.buildRoot(this.editor.getDocument().get(), getProject().getDetails());
		this.root=buildRoot;
		return buildRoot;
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		if (formEditor.isDirty()) {
			formEditor.updateText();
		}
		getEditor(0).doSave(monitor);
		//getEditor(1).doSave(monitor);

		validate();
	}

	public void validate() {
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
				ErrorVisitor st = new ErrorVisitor(file,string);
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

	public void select(int start, int end) {
		this.editor.selectAndReveal(start, end-start);
	}

}
