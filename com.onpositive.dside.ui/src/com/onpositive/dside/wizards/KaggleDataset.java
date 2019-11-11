package com.onpositive.dside.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.python.pydev.core.log.Log;
import org.yaml.snakeyaml.Yaml;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.dside.tasks.GateWayRelatedTask;
import com.onpositive.dside.tasks.IGateWayServerTaskDelegate;
import com.onpositive.dside.tasks.TaskManager;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.project.DataProjectAccess;
import com.onpositive.musket_core.IServer;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class KaggleDataset extends Wizard implements INewWizard {
	private static final String DEPS_FILE = "project.yaml";
	
	private IStructuredSelection selection;

	private KaggleDatasetParams datasetView;

	public KaggleDataset() {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		this.addPage(new DLFWizardPage("Hello") {

			@SuppressWarnings("serial")
			@Override
			public void createControl(Composite parent) {
				setImageDescriptor(SWTImageManager.getDescriptor("dataset_wiz"));
				el = new RootElement(parent);
				setTitle("New Dataset");
				setMessage("Let's have fun");

				datasetView = new KaggleDatasetParams();

				ISelection selection2 = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getSelection();

				if (selection2 instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection2).getFirstElement();

					if (firstElement instanceof IAdaptable) {
						IAdaptable mm = (IAdaptable) firstElement;
						IResource adapter = mm.getAdapter(IResource.class);
						if (adapter != null) {
							org.eclipse.core.resources.IProject prj = adapter.getProject();
							datasetView.project = prj.getName();

							try {
								readConfig(prj, datasetView);
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}
				}

				Binding bn = new Binding(datasetView);

				IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(datasetView, null, null);

				IUIElement<?> createWidget = widgetObject.createWidget(bn);

				el.add((AbstractUIElement<?>) createWidget);

				setControl((Control) createWidget.getControl());

				this.setPageComplete(false);

				bn.addValidator(new IValidator<Object>() {
					@Override
					public CodeAndMessage isValid(IValidationContext arg0, Object arg1) {
						return datasetView.getItem() == null
								? new CodeAndMessage(CodeAndMessage.ERROR, "Selection is empty!")
								: new CodeAndMessage(CodeAndMessage.OK, "");
					}

				});

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
	
	private static List<DataLink> loadYaml(org.eclipse.core.resources.IProject project) {
		String projectPath = project.getLocation().toOSString();
		
		IFile depsFile = project.getFile(DEPS_FILE);
		
		Yaml yaml = new Yaml();
		
		Map<String, Object> parsed = null;
		
		List<Object> parsedDeps = new ArrayList<Object>();
		
		try {
			if(depsFile.exists()) {
				parsed = (Map<String, Object>) yaml.load(depsFile.getContents());
				
				parsedDeps = (List<Object>) parsed.get("dependencies");
			}
		} catch (Throwable t) {
			parsedDeps = new ArrayList<Object>();
		}
		
		List<DataLink> result = new ArrayList<DataLink>();
		
		for(Object item : parsedDeps) {
			result.add(new DataLink(item));
		}
		
		return result;
	}

	private void download(org.eclipse.core.resources.IProject project) {
		IFolder folder = project.getFolder("data");

		String fullPath = folder.getLocation().toOSString();

		GateWayRelatedTask serverTask = new GateWayRelatedTask(project, new IGateWayServerTaskDelegate() {
			@Override
			public void terminated() {

			}

			@Override
			public void started(GateWayRelatedTask task) {

			}
		});

		serverTask.getServer().thenAcceptAsync((IServer server) -> {
			try {
				if (datasetView.isDsDatasetEnabled()) {
					server.downloadDataset(datasetView.getItem().ref, fullPath);
				} else {
					server.downloadCompetitionFiles(datasetView.getItem().ref, fullPath);
				}

			} catch (Throwable t) {
				t.printStackTrace();
			}

			serverTask.terminate();
			try {
				folder.refreshLocal(1, new NullProgressMonitor());
				folder.accept(new IResourceVisitor() {

					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource instanceof IFile && resource.getName().endsWith(".csv")) {
							String persistentProperty = resource.getPersistentProperty(IDE.EDITOR_KEY);
							if (persistentProperty != null) {
								return false;
							}
							try {
								IDataSet dataSet = DataProjectAccess.getDataSet(resource.getLocation().toFile(),null);
								if (dataSet != null) {
									resource.setPersistentProperty(IDE.EDITOR_KEY,
											"com.onpositive.datasets.visualisation.ui.datasetEditor");
									resource.refreshLocal(0, new NullProgressMonitor());
								}
							} catch (Exception e) {
								e.printStackTrace();
								// TODO: handle exception
							}
						}
						if (resource.equals(folder)) {
							return true;
						}
						return false;
					}
				});
				folder.refreshLocal(1, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		});

		if (!datasetView.dsSkipDownload) {
			TaskManager.perform(serverTask);
		}

		try {
			writeConfig(project, datasetView);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static KaggleDatasetParams readConfig(IProject project, KaggleDatasetParams config)
			throws IOException, CoreException {
		IFolder metadata = project.getFolder(".metadata");
		
		if (!metadata.exists()) {
			metadata.create(true, true, null);
		}

		IFile metadataFile = project.getFile(".metadata/kaggle-datasource-metadata.json");

		if (metadataFile.exists()) {
			InputStreamReader reader = new InputStreamReader(metadataFile.getContents());
			
			BufferedReader br = new BufferedReader(reader);
			
			String line = br.readLine();
			
			String jsonString = "";
			
			while(line != null) {
				jsonString += line + "\n";

				line = br.readLine();
			}
			
			config.deserializeFromJsonString(jsonString);
			
			for(DataLink link: loadYaml(project)) {
				if(link.isKaggle()) {
					config.applyDataLink(link);
					
					break;
				}
			}
		}
		
		return config;
	}
	
	private Map<String, Object> serializeDataLinks(Map<String,Object> result,List<DataLink> links) {
		
		
		List<Object> deps = new ArrayList<>();
		
		result.put("dependencies", deps);
		
		for(DataLink item: links) {
			deps.add(item.serialize());
		}
		
		return result;
	}

	private void writeConfig(IProject project, KaggleDatasetParams config) throws CoreException, IOException {
		IFolder metadata = project.getFolder(".metadata");

		if (!metadata.exists()) {
			metadata.create(true, true, null);
		}

		IFile metadataFile = project.getFile(".metadata/kaggle-datasource-metadata.json");

		if (metadataFile.exists()) {
			metadataFile.delete(true, null);
		}

		String jsonString = config.serializeToJsonString();

		ByteArrayInputStream bin = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

		metadataFile.create(bin, true, null);
		
		List<DataLink> dataLinks = loadYaml(project);
		
		DataLink link = config.asDatalink();
		
		for(DataLink item: dataLinks) {
			if(link.equals(item)) {
				dataLinks.remove(item);
				
				break;
			}
		}
		
		dataLinks.add(0, link);
		
		Yaml yaml = new Yaml();
		
		IFile depsFile = project.getFile(DEPS_FILE);
		Map<String,Object>parsed=new LinkedHashMap<String, Object>();
		try {
			if(depsFile.exists()) {
				parsed = (Map<String, Object>) yaml.load(depsFile.getContents());
				
				
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		String txt = yaml.dump(serializeDataLinks(parsed,dataLinks));
		
		bin = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
		
		
		
		if(depsFile.exists()) {
			depsFile.setContents(bin, true, true, null);
		}
				
		depsFile.create(bin, true, null);
	}

	private void ensure(IFolder folder, IProgressMonitor monitor) throws CoreException {
		List<IContainer> folders = new ArrayList<IContainer>();

		IContainer currentFolder = folder;

		while (!currentFolder.exists()) {
			folders.add(0, currentFolder);

			currentFolder = (IContainer) currentFolder.getParent();
		}

		for (IContainer cnt : folders) {
			if (cnt instanceof IFolder) {
				((IFolder) cnt).create(true, true, monitor);
			}
		}
	}

	@Override
	public boolean performFinish() {
		Display currentDisplay = Display.getCurrent();

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException {
				org.eclipse.core.resources.IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(datasetView.project);

				IFolder folder = project.getFolder("data");

				ensure(folder, monitor);

				currentDisplay.asyncExec(new Runnable() {
					public void run() {
						download(project);
					}
				});
			}
		};

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
