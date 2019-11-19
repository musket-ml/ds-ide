
package com.onpositive.dside.ui.editors;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.dside.tasks.analize.AnalizeData;
import com.onpositive.dside.tasks.analize.AnalizeDataSet;
import com.onpositive.dside.ui.DynamicUI;
import com.onpositive.dside.ui.ExperimentsView;
import com.onpositive.dside.ui.HumanCaption;
import com.onpositive.dside.ui.TaskConfiguration;
import com.onpositive.musket_core.Experiment;
import com.onpositive.musket_core.ProjectWrapper;
import com.onpositive.musket_core.ValidateTask;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.yamledit.introspection.InstrospectedFeature;

public class EditorTasks {

	public static class LaunchExperiment extends EditorTask {

		public LaunchExperiment() {
			super("Launch Experiment", "run_experiment");
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			editor.doSave(new NullProgressMonitor());
			ExperimentsView.launchExperiment(exp);
		}

	}

	public static class ValidateModelTask extends EditorTask {

		public ValidateModelTask() {
			super("Validate", "validate");
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			ValidateTask validateTask = new ValidateTask(exp);
			TaskManager.perform(validateTask);
		}
	}
	public static class DuplicateExperimentTask extends EditorTask {

		public DuplicateExperimentTask() {
			super("Create Duplicate", "com.onpositive.semantic.ui.copy");
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			ExperimentsView.duplateExperiment(exp);
		}
	}
	
	public static class UserTask extends EditorTask{

		private InstrospectedFeature task;

		public UserTask(InstrospectedFeature task) {
			super(HumanCaption.getHumanCaption(task.getName()), "generic_task");
			this.task=task;
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			editor.doSave(new NullProgressMonitor());
			Map<String, Object> open = new DynamicUI(task).open(exp);
			if (open != null) {
				TaskConfiguration cfg = new TaskConfiguration(Collections.singleton(exp));
				cfg.setTaskArgs(open);
				if (open.containsKey("debug")) {
					cfg.setDebug((Boolean) open.get("debug"));
				}
				if (open.containsKey("workers")) {
					cfg.setNumWorkers(Integer.parseInt(open.get("workers").toString()));
				}
				cfg.setTasks(task.getName());
				TaskManager.perform(cfg);
			}
		}		
	}

	public static class AnalizePredictionsTask extends EditorTask {

		public AnalizePredictionsTask() {
			super("Analize Predictions", "analize");
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			AnalizeDataSet initial = new AnalizeDataSet(exp);
			boolean createObject = WidgetRegistry.createObject(initial);
			if (createObject) {
				FileEditorInput editorInput = (FileEditorInput) editor.getEditorInput();
				GateWayRelatedTask gateWayRelatedTask = new GateWayRelatedTask(editorInput.getFile().getProject(),
						initial);
				gateWayRelatedTask.setDebug(initial.isDebug());
				TaskManager.perform(gateWayRelatedTask);
			}
		}
	}
	public static class AnalizeDataTask extends EditorTask {

		public AnalizeDataTask() {
			super("Analize Data", "analize_data");
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			exp.readConfig();
			AnalizeDataSet initial = new AnalizeData(exp);
			initial.data=true;
			boolean createObject = WidgetRegistry.createObject(initial);
			if (createObject) {
				FileEditorInput editorInput = (FileEditorInput) editor.getEditorInput();
				GateWayRelatedTask gateWayRelatedTask = new GateWayRelatedTask(editorInput.getFile().getProject(),
						initial);
				gateWayRelatedTask.setDebug(initial.isDebug());
				TaskManager.perform(gateWayRelatedTask);
			}
		}
	}
	public static class RegreshMetadata extends EditorTask{

		public RegreshMetadata() {
			super("Refresh", "refresh");
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			editor.getProject().refresh(null);
		}
		
	}
	
	public static class ExportTask extends EditorTask{

		public ExportTask() {
			super("Export", "export");
		}

		@Override
		public void perform(ExperimentOverivewEditorPart editor, Experiment exp) {
			ExportOptions opts=new ExportOptions();
			ProjectWrapper project = editor.getProject();
			IContainer containerForLocation = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(project.getPath()));
			boolean createObject = WidgetRegistry.createObject(opts);
			if (createObject) {
				opts.selected.run((IProject) containerForLocation, exp.getPath().toPortableString());
			}
		}
		
	}

	public static EditorTask[] getTasks() {
		return new EditorTask[] { new LaunchExperiment(), new ValidateModelTask(), new AnalizeDataTask(), new AnalizePredictionsTask(), new DuplicateExperimentTask(),new RegreshMetadata() , new ExportTask()};
	}
}
