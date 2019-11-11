package com.onpositive.dside.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.navigator.CommonNavigator;
import org.python.pydev.core.log.Log;
import org.python.pydev.shared_ui.EditorUtils;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.datasets.visualisation.ui.views.CSVDataSetEditor;
import com.onpositive.datasets.visualisation.ui.views.ClassificationTemplate;
import com.onpositive.datasets.visualisation.ui.views.GenericExperimentTemplate;
import com.onpositive.datasets.visualisation.ui.views.InstanceSegmentationTemplate;
import com.onpositive.datasets.visualisation.ui.views.SegmentationTemplate;
import com.onpositive.datasets.visualisation.ui.views.TextClassificationTemplate;
import com.onpositive.datasets.visualisation.ui.views.TextSequenceTemplate;
import com.onpositive.dside.ui.IMusketConstants;
import com.onpositive.dside.ui.navigator.ExperimentGroup;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.project.DataProjectAccess;
import com.onpositive.musket_core.ProjectManager;
import com.onpositive.musket_core.ProjectWrapper;
import com.onpositive.musket_core.ProjectWrapper.BasicDataSetDesc;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class NewMusketExperimentWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;

	public NewMusketExperimentWizard() {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	private ExperimentParams experimentParams;

	private org.eclipse.core.resources.IProject prj;

	IFile dataSetFile;

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		this.addPage(new DLFWizardPage("Hello") {

			
			
			@Override
			public void createControl(Composite parent) {
				setImageDescriptor(SWTImageManager.getDescriptor("new_exp_wiz"));
				el = new RootElement(parent);
				
				setTitle("New Experiment");
				setMessage("Let's have fun");
				experimentParams = new ExperimentParams();
				ISelection selection2 = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getSelection();
				if (selection2 instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection2).getFirstElement();
					if (firstElement instanceof ExperimentGroup) {
						ExperimentGroup g = (ExperimentGroup) firstElement;
						experimentParams.group = g.getPath().toPortableString();
					}
					if (firstElement instanceof IAdaptable) {
						IAdaptable mm = (IAdaptable) firstElement;
						IResource adapter = mm.getAdapter(IResource.class);
						if (adapter != null) {
							prj = adapter.getProject();
							experimentParams.project = prj.getName();
						}
					}
				}
				if (prj != null) {
					ArrayList<BasicDataSetDesc> dataSets = ProjectManager.getInstance(prj).getDataSets();
					experimentParams.possibleDataSets = new ArrayList<>(
							dataSets.stream().map(x -> x.name).collect(Collectors.toList()));
					experimentParams.datasets = dataSets;
					// System.out.println(dataSets);
				}
				Binding bn = new Binding(experimentParams);

				IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(experimentParams, null,
						null);
				IUIElement<?> createWidget = widgetObject.createWidget(bn);
				el.add((AbstractUIElement<?>) createWidget);
				setControl((Control) createWidget.getControl());
				this.setPageComplete(false);
				bn.addStatusChangeListener(new IStatusChangeListener() {

					@Override
					public void statusChanged(IHasStatus bnd, CodeAndMessage cm) {
						setPageComplete(!cm.isError());
						setErrorMessage(cm.getMessage());
					}
				});
				setErrorMessage(bn.getStatus().getMessage());
			}
		});
	}

	@Override
	public boolean performFinish() {
		IFile fl = null;
		String dsName="myDataset: []";
		if (experimentParams.dataset != null && experimentParams.dataset.trim().length() > 0) {
			for (BasicDataSetDesc d : experimentParams.datasets) {
				if (d.name.equals(experimentParams.dataset)) {
					IFile file = prj.getFolder("data").getFile(d.origin);
					if (file != null && file.exists()) {
						dataSetFile = file;
					}
					if (dataSetFile != null) {

					}
					if (d.origin != null && d.kind != null) {
						if (prj != null) {
							fl = prj.getFolder("data").getFile(d.origin);
						}
						// Now we can modify our wizard;
					}
					if (d.functionName!=null) {
						dsName=d.functionName+": []";
					}
					else {
						dsName="get"+d.name+": []";
					}
				}
			}
		}
		Template template = TemplatesList.getTemplatesList().getTemplates().stream()
				.filter(x -> x.name.equals(experimentParams.template)).findFirst().get();
		GenericExperimentTemplate rs = null;

		if (template.kind.equals("classification")) {
			rs = new ClassificationTemplate();
		}
		else if (template.kind.equals("segmentation")) {
			rs = new SegmentationTemplate();
		}
		else if (template.kind.equals("text_classification")) {
			rs = new TextClassificationTemplate();
		}
		else if (template.kind.equals("text_seq_classification")) {
			rs = new TextSequenceTemplate();
		}
		else if(template.kind.equals("instance_segmentation")){
			ProjectWrapper wrapper = ProjectManager.getInstance(prj);
			rs = new InstanceSegmentationTemplate(wrapper, prj);
		}
		GenericExperimentTemplate ft=rs;
		if (rs != null) {
			IFile x=fl;
			String fDName=dsName;
			Display.getCurrent().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					IDataSet ds = null;
					try {
						if (x != null && x.exists()) {
							ds=DataProjectAccess.getDataSet(x.getLocation().toFile(),null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					CSVDataSetEditor.configureFromDataSetAndTemplate(prj, experimentParams.name, ds, ft,fDName);
					
					
				}
			});
			return true;
			
		}
		String dN=dsName;
		// define the operation to create a new project
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException {
				org.eclipse.core.resources.IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(experimentParams.project);
				IFolder folder = project.getFolder("experiments");
				if (!folder.exists()) {
					folder.create(true, true, monitor);
				}
				String group = experimentParams.group;
				if (group == null) {
					group = "";
				}
				Path path = new Path(group);
				IFolder folder2 = folder;
				if (!path.isEmpty()) {
					folder2 = folder.getFolder(path);
					if (!folder2.exists()) {
						folder2.create(true, true, monitor);
					}
				}
				IFolder folder3 = folder2.getFolder(new Path(experimentParams.name));
				if (!folder3.exists()) {
					folder3.create(true, true, monitor);
				}
				IFile file = folder3.getFile(IMusketConstants.MUSKET_CONFIG_FILE_NAME);
				Template template = TemplatesList.getTemplatesList().getTemplates().stream()
						.filter(x -> x.name.equals(experimentParams.template)).findFirst().get();
				InputStream resourceAsStream = NewMusketExperimentWizard.class.getResourceAsStream("/templates/" + template.file);
				StringBuilder bld=new StringBuilder();
				try {
				InputStreamReader rs=new InputStreamReader(resourceAsStream);
				BufferedReader r=new BufferedReader(rs);
				
				while (true) {
					String readLine;
					try {
						readLine = r.readLine();
						if (readLine==null) {
							break;
						}
						bld.append(readLine+System.lineSeparator());
					} catch (IOException e) {
						break;
					}
					
				}
				}finally {
					try {
						resourceAsStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				String content=bld.toString();
				if (content.contains("getData: []")&&dN!=null&&!dN.isEmpty()) {
					content=content.replace("getData: []", dN);
				}
				file.create(new ByteArrayInputStream(content.getBytes()), true,
						monitor);
				Display.getDefault().asyncExec(() -> {
					EditorUtils.openFile(file);
				});

			}
		};

		// run the operation to create a new project
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					MessageDialog.openError(getShell(), "Unable to create experiment",
							"Another experiement with the same name already exists.");
				} else {
					ErrorDialog.openError(getShell(), "Unable to create project", null,
							((CoreException) t).getStatus());
				}
			} else {
				// Unexpected runtime exceptions and errors may still occur.
				Log.log(IStatus.ERROR, t.toString(), t);
				MessageDialog.openError(getShell(), "Unable to create experiment", t.getMessage());
			}
			return false;
		}
		CommonNavigator activePart = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView("org.python.pydev.navigator.view");
		if (activePart != null) {
			activePart.getCommonViewer().refresh();
		}
		return true;
	}

}
