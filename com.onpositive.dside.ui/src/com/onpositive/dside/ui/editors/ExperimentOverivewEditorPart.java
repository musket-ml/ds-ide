package com.onpositive.dside.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.EditorPart;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.LinkElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ProjectManager;
import com.onpositive.musket_core.ProjectWrapper;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.HyperlinkEvent;
import com.onpositive.semantic.model.ui.generic.IHyperlinkListener;
import com.onpositive.semantic.model.ui.property.editors.SectionEditor;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.yamledit.introspection.InstrospectionResult;
import com.onpositive.yamledit.model.INodeListener;
import com.onpositive.yamledit.model.ModelNode;
import com.onpositive.yamledit.model.NodeKind;

import de.jcup.yamleditor.YamlEditor;

public class ExperimentOverivewEditorPart extends EditorPart implements INodeListener {

	private Binding binding;
	boolean dirty;
	boolean disposed;
	private Experiment exp;
	private TextEditor experiment;

	private ExperimentMultiPageEditor mainEditor;

	private EditorModel model;

	private ProjectWrapper project;

	private Runnable refreshListener;

	private Container uiRoot;

	public ExperimentOverivewEditorPart(TextEditor editor, Experiment exp,ExperimentMultiPageEditor mainEditor) {
		this.experiment = editor;
		this.mainEditor=mainEditor;
		this.exp = exp;
		project = ProjectManager.getInstance().getProject(exp);
		refreshListener = () -> {
			InstrospectionResult details = project.getDetails();
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					introspected(details);
				}
			});

		};
		project.addRefreshListener(refreshListener);
		project.refresh(null);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		RootElement rl = new RootElement(parent);
		NodeKind kind = new NodeKind("basicConfig", this);
		model = new EditorModel(experiment, kind);
		IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(model, null, null);
		binding = new Binding(model);
		uiRoot = (Container) widgetObject.createWidget(binding);
		rl.add((AbstractUIElement<?>) uiRoot);
		populateTasks(uiRoot);
	}

	@Override
	public void dispose() {
		project.removeRefreshListener(refreshListener);
		disposed = true;		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		this.dirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {

	}

	public ProjectWrapper getProject() {
		return project;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setInput(input);
		super.setSite(site);
	}
	private void introspected(InstrospectionResult details) {
		if (this.disposed) {
			return;
		}
		this.populateTasks(uiRoot);
		
		if (uiRoot!=null) {
			uiRoot.getContentParent().layout(true, true);
		}
		this.mainEditor.validate();
	}
	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void populateTasks(Container createWidget) {
//		SectionEditor element = (SectionEditor) createWidget.getElement("tasks");
//		element.setLayoutManager(null);
//		RowLayout layout = new RowLayout();
//		layout.wrap = true;
//		element.setLayout(layout);
//		List<AbstractUIElement<?>> children = new ArrayList<>(element.getChildren());
//		children.forEach(r -> element.remove(r));
//		for (EditorTask task : EditorTasks.getTasks()) {
//			representTask(element, task);
//		}
//
//		List<InstrospectedFeature> tasks = project.getTasks();
//		tasks.forEach(r -> {
//			representTask(element, new EditorTasks.UserTask(r));
//		});
//		element.getControl().layout(true, true);

	}

	private void representTask(SectionEditor element, EditorTask task) {
		LinkElement element2 = new LinkElement();
		element2.setCaption(task.name);
		element2.setIcon(task.image);
		element2.addHyperLinkListener(new IHyperlinkListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void linkActivated(HyperlinkEvent e) {
				task.perform(ExperimentOverivewEditorPart.this, exp);
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {

			}

			@Override
			public void linkExited(HyperlinkEvent e) {

			}
		});
		element.add(element2);
	}

	@Override
	public void setFocus() {
	}

	public void setProject(ProjectWrapper project) {
		this.project = project;
	}

	@Override
	public void updated(ModelNode node, Object newValue, String property) {
		this.dirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	public void updateFromText() {
		if (model != null) {
			model.update(((YamlEditor) experiment).getDocument().get());
			binding.refresh(true);
		}

	}

	public void updateText() {
		if (dirty) {
			String string = model.getRoot().toString();
			((YamlEditor) experiment).getDocument().set(string);
			this.dirty = false;
			firePropertyChange(PROP_DIRTY);
		}
	}

}
