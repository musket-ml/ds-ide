package com.onpositive.dside.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.compare.internal.CompareAction;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ExperimentFinder;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.semantic.ui.workbench.elements.XMLView;

public class ExperimentsView extends XMLView {

	private final class MockAction extends org.eclipse.jface.action.Action implements IAction{
		
	}

	Collection<Experiment> experiments = new ArrayList<Experiment>();
	
	private List<IContainer> containers;

	public ExperimentsView() {
		super("dlf/experiments.dlf");
	}

	@Override
	public void setFocus() {

	}
	
	
	public void setLocation(List<IContainer> find) {
		this.containers=find;
		Collection<Experiment> ex = ExperimentFinder.find(find);
		this.setExperiments(ex);
	}

	public void setExperiments(Collection<Experiment> find) {
		experiments = new ArrayList<>();
		ObjectChangeManager.markChanged(this);
		this.experiments = find;
		try {
			ObjectChangeManager.markChanged(this);
			ObjectChangeManager.markChanged((Object) this.experiments);
		} catch (Exception e) {
			DSIDEUIPlugin.log(e);
		}
	}
	
	Runnable launchListener=new Runnable() {
		
		@Override
		public void run() {
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					fullRefresh();
				}
			});
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		TaskManager.addJobListener(launchListener);
		AbstractEnumeratedValueSelector<?> element = (AbstractEnumeratedValueSelector<?>) getElement("e");
		element.getViewer().addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				open();
			}
		});

	}
	@Override
	public void dispose() {
		TaskManager.removeJobListener(launchListener);
		super.dispose();
	}

	public void open() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		Collection<Object> collection = ValueUtils.toCollection(currentValue);
		for (Object o : collection) {
			Experiment e = (Experiment) o;

			open(e);
		}

	}

	public static void open(Experiment e) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(e.getPath())
				.getFile(new Path(IMusketConstants.MUSKET_CONFIG_FILE_NAME));
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
					new FileEditorInput(file), "com.onpositive.dside.ui.editors.ExperimentMultiPageEditor");
		} catch (Exception e1) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e1.getMessage());
		}
	}
	
	public void launch() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		launchExperiment(currentValue);
	}

	public static void launchExperiment(Object currentValue) {
		Collection<Experiment> collection = Collections.singletonList((Experiment)currentValue);
		LaunchConfiguration cfg=new LaunchConfiguration(collection);
		boolean createObject = WidgetRegistry.createObject(cfg);
		if (createObject) {
			for (Experiment e:cfg.experiment) {
				e.backup(true);
				if (cfg.cleanSplits&&!cfg.onlyReports) {
					new File(e.getPath().toFile(),"config.yaml.folds_split").delete();
					new File(e.getPath().toFile(),"config.yaml.holdout_split").delete();					
				}
				new File(e.getPath().toFile(),"error.yaml").delete();
			}
			
			//String collect = collection.stream().map(x->x.toString()).collect(Collectors.joining(","));
			TaskManager.perform(cfg);
		}
	}
	public void task() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		Collection<Object> collection = ValueUtils.toCollection(currentValue); 
		TaskConfiguration cfg=new TaskConfiguration(collection);
		boolean createObject = WidgetRegistry.createObject(cfg);
		if (createObject) {
			//String collect = collection.stream().map(x->x.toString()).collect(Collectors.joining(","));
			TaskManager.perform(cfg);
		}
	}
	
	public void compare() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		Collection<Object> collection = ValueUtils.toCollection(currentValue);
		ArrayList<Object>objects=new ArrayList<>();
		for (Object o : collection) {
			Experiment e = (Experiment) o;
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(e.getPath())
					.getFile(new Path(IMusketConstants.MUSKET_CONFIG_FILE_NAME));
			objects.add(file);
		}
		CompareAction compareAction = new CompareAction();
		
		StructuredSelection selection = new StructuredSelection(objects);
		MockAction action = new MockAction();
		compareAction.setActivePart(action,this);
		compareAction.selectionChanged(action, selection);
		if (action.isEnabled()) {
			compareAction.run(selection);
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Can not compare", "Only two experiments can be compared");
		}		
	}
	public void run() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		Collection<Object> collection = ValueUtils.toCollection(currentValue);
		ArrayList<Object>objects=new ArrayList<>();
		for (Object o : collection) {
			Experiment e = (Experiment) o;
			//TODO finish this
		}
				
	}

	public void duplicate() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		Collection<Object> collection = ValueUtils.toCollection(currentValue);
		for (Object o : collection) {
			Experiment e = (Experiment) o;
			Experiment exp=duplateExperiment(e);
			if (exp!=null) {
			this.experiments.add(exp);
			}
		}
		fullRefresh();
	}

	public static Experiment duplateExperiment(Experiment e) {
		IInputValidator validator = new IInputValidator() {

			@Override
			public String isValid(String newText) {
				if (newText.isEmpty()) {
					return "Experiment should be named";
				}
				return null;
			}
		};
		InputDialog inputDialog = new InputDialog(Display.getCurrent().getActiveShell(), "Duplicate Experiment",
				"Please provide new name for dublicate of: " + e.toString(), "", validator);
		inputDialog.open();
		String value = inputDialog.getValue();
		if (value != null && value.length() > 0) {
			Experiment exp = e.duplicate(value);
			if (exp != null) {
				
				
				
				
				open(exp);
				return exp;
			}
		}
		return null;
	}

	public void deleteExperiment() {
		ISelectorElement<?> el = (ISelectorElement<?>) getElement("e");
		Object currentValue = el.getSelectionBinding().getValue();
		Collection<Object> collection = ValueUtils.toCollection(currentValue);
		MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(), "Delete experiments", null,
				"Are you sure you want to delete experiments: " + collection.toString(), MessageDialog.ERROR,
				new String[] { "Ok", "Cancel" }, 0);
		int result = dialog.open();
		if (result == 0) {
			for (Object o : collection) {
				if (o instanceof Experiment) {
					if (((Experiment) o).delete()) {
						this.experiments.remove(o);
					} else {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
								"Some experiments was not deleted properly");
					}
				}
			}
			this.getBinding("experiments").refresh();
		}
		fullRefresh();
	}

	private void fullRefresh() {
		
		if (containers!=null) {
			setLocation(containers);
		}
	}
}
