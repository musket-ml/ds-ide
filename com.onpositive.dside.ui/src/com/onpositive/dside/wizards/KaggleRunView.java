package com.onpositive.dside.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.dside.ui.DatasetTableElement;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class KaggleRunView extends Wizard implements INewWizard {
	private KaggleRunConfig kaggleConfig;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
				
	}
	
	@Override
	public void addPages() {
		this.addPage(new WizardPage("Run on Kaggle Kernels") {
			@Override
			public void createControl(Composite parent) {
				setImageDescriptor(SWTImageManager.getDescriptor("dataset_wiz"));
				
				RootElement el = new RootElement(parent);
				
				setTitle("New Dataset");
				
				setMessage("Let's have fun");
				
				kaggleConfig = new KaggleRunConfig();
								
				ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
				
				if(selection instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					
					if (firstElement instanceof IAdaptable) {
						IAdaptable firstElementAsAdaptable = (IAdaptable) firstElement;
						
						IResource adapter = firstElementAsAdaptable.getAdapter(IResource.class);
						
						if (adapter != null) {
							kaggleConfig.project = adapter.getProject().getName();
							
							try {
								readConfig(adapter.getProject(), kaggleConfig);
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}
				}
				
				Binding bn = new Binding(kaggleConfig);

				IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(kaggleConfig, null, null);
				
				IUIElement<?> createWidget = widgetObject.createWidget(bn);
				
				el.add((AbstractUIElement<?>) createWidget);
				
				setControl((Control) createWidget.getControl());
				
				this.setPageComplete(false);
				
				bn.addValidator(new IValidator<Object>() {
					@Override
					public CodeAndMessage isValid(IValidationContext arg0, Object arg1) {
						return kaggleConfig.datasourceType.length() == 0 ? new CodeAndMessage(CodeAndMessage.ERROR, "Datasource is not defined!") : new CodeAndMessage(CodeAndMessage.OK, "");
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
	
	private void readConfig(IProject project, KaggleRunConfig config) throws IOException, CoreException {
		IFile metadataFile = project.getFile(".metadata/kaggle-project-metadata.json");
				
		if(metadataFile.exists()) {
			InputStreamReader reader = new InputStreamReader(metadataFile.getContents());
			
			BufferedReader br = new BufferedReader(reader);
			
			String line = br.readLine();
			
			String jsonString = "";
			
			while(line != null) {
				jsonString += line + "\n";
				
				line = br.readLine();
			}
			
			config.deserializeFromJsonString(jsonString);
		}
		
		KaggleDatasetParams datasetParams = new KaggleDatasetParams();
		
		KaggleDataset.readConfig(project, datasetParams);
		
		DatasetTableElement datasource = datasetParams.getItem();
		
		if(datasource != null) {
			config.datasourceType = datasetParams.dsIsDataset ? "dataset" : "competition";
			
			config.datasource = datasource.ref;
		}
	}
	
	private void writeConfig(IProject project, KaggleRunConfig config) throws IOException, CoreException {
		IFile metadataFile = project.getFile(".metadata/kaggle-project-metadata.json");
				
		if(metadataFile.exists()) {
			metadataFile.delete(true, null);
		}
		
		String jsonString = config.serializeToJsonString();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
		
		metadataFile.create(bin, true, null);
	}
	
	@Override
	public boolean performFinish() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(kaggleConfig.project);
		
		try {
			writeConfig(project, kaggleConfig);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		return true;
	}
}
