package com.onpositive.dside.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.onpositive.dside.ui.editors.outline.OutlineContentProvider;
import com.onpositive.dside.ui.editors.outline.OutlineLabelProvider;
import com.onpositive.dside.ui.editors.outline.OutlineNode;

import de.jcup.yamleditor.EclipseUtil;
import de.jcup.yamleditor.YamlEditorActivator;
import de.jcup.yamleditor.outline.YamlEditorOutlineLabelProvider;

public class ExperimentOutline extends ContentOutlinePage implements IContentOutlinePage,ISelectionChangedListener,IDoubleClickListener{

	private static ImageDescriptor IMG_DESC_LINKED = EclipseUtil.createImageDescriptor("/icons/outline/synced.png",
			YamlEditorActivator.PLUGIN_ID);
	private static ImageDescriptor IMG_DESC_NOT_LINKED = EclipseUtil
			.createImageDescriptor("/icons/outline/sync_broken.png", YamlEditorActivator.PLUGIN_ID);

	private OutlineContentProvider contentProvider;
	private Object input;
	private ExperimentMultiPageEditor editor;
	private YamlEditorOutlineLabelProvider labelProvider;

	private boolean linkingWithEditorEnabled;
	private boolean ignoreNextSelectionEvents;
	private ToggleLinkingAction toggleLinkingAction;

	public ExperimentOutline(ExperimentMultiPageEditor editor) {
		this.editor = editor;
		this.contentProvider = new OutlineContentProvider();
	}

	public OutlineContentProvider getContentProvider() {
		return contentProvider;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		labelProvider = new YamlEditorOutlineLabelProvider();

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(contentProvider);
		viewer.addDoubleClickListener(this);
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));
		viewer.addSelectionChangedListener(this);
		/* next line enables tooltips for tree viewer- necesseary, otherwise not working!*/
		ColumnViewerToolTipSupport.enableFor(viewer);

		/* it can happen that input is already updated before control created */
		if (input != null) {
			viewer.setInput(input);
		}
		toggleLinkingAction = new ToggleLinkingAction();
		toggleLinkingAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR);
		
		IActionBars actionBars = getSite().getActionBars();
		
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(toggleLinkingAction);
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new OutlineLabelProvider()));
		IMenuManager viewMenuManager = actionBars.getMenuManager();
		viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$
	
		viewMenuManager.add(new Separator("treeGroup")); //$NON-NLS-1$
		viewMenuManager.add(toggleLinkingAction);
		
		this.input=editor.getRoot();
		viewer.setInput(this.input);
		viewer.expandAll();
//		/*
//		 * when no input is set on init state - let the editor rebuild outline
//		 * (async)
//		 */
//		if (input == null && editor != null) {
//			editor.rebuildOutline();
//		}

	}
	
	

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (editor == null) {
			return;
		}
		if (linkingWithEditorEnabled) {
			editor.setFocus();
			// selection itself is already handled by single click
			return;
		}
		ISelection selection = event.getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection sl=(StructuredSelection) selection;
			if (!sl.isEmpty()) {
				OutlineNode firstElement = (OutlineNode) sl.getFirstElement();
				int start=firstElement.getStart();
				int end=firstElement.getEnd();
				editor.select(start,start);
			}
		}
		//editor.openSelectedTreeItemInEditor(selection, true);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		if (!linkingWithEditorEnabled) {
			return;
		}
		if (ignoreNextSelectionEvents) {
			return;
		}
		ISelection selection = event.getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection sl=(StructuredSelection) selection;
			if (!sl.isEmpty()) {
				OutlineNode firstElement = (OutlineNode) sl.getFirstElement();
				int start=firstElement.getStart();
				int end=firstElement.getEnd();
				editor.select(start,end);
			}
		}
		//editor.openSelectedTreeItemInEditor(selection, false);
	}

	public void onEditorCaretMoved(int caretOffset) {
		if (!linkingWithEditorEnabled) {
			return;
		}
		ignoreNextSelectionEvents = true;
		if (contentProvider instanceof OutlineContentProvider) {
			OutlineContentProvider gcp = (OutlineContentProvider) contentProvider;
			Object item = gcp.tryToFindByOffset(caretOffset);
			if (item != null) {
				StructuredSelection selection = new StructuredSelection(item);
				getTreeViewer().setSelection(selection, true);
			}
		}
		ignoreNextSelectionEvents = false;
	}

	

	class ToggleLinkingAction extends Action {

		private ToggleLinkingAction() {
			if (editor != null) {
				linkingWithEditorEnabled = true;
			}
			setDescription("link with editor");
			initImage();
			initText();
		}

		@Override
		public void run() {
			linkingWithEditorEnabled = !linkingWithEditorEnabled;

			initText();
			initImage();
		}

		private void initImage() {
			setImageDescriptor(
					linkingWithEditorEnabled ? getImageDescriptionForLinked() : getImageDescriptionNotLinked());
		}

		private void initText() {
			setText(linkingWithEditorEnabled ? "Click to unlink from editor" : "Click to link with editor");
		}

	}

	protected ImageDescriptor getImageDescriptionForLinked() {
		return IMG_DESC_LINKED;
	}

	protected ImageDescriptor getImageDescriptionNotLinked() {
		return IMG_DESC_NOT_LINKED;
	}

	public void refresh() {
		this.input=editor.getRoot();
		getTreeViewer().setInput(this.input);
		getTreeViewer().expandAll();
	}
}
