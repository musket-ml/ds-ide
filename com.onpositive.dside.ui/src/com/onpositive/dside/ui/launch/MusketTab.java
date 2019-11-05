package com.onpositive.dside.ui.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.onpositive.dside.tasks.ITaskConstants;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.LaunchConfiguration;
import com.onpositive.yamledit.io.YamlIO;

public class MusketTab extends AbstractLaunchConfigurationTab {

	private LaunchConfiguration launchConfiguration;
	private Spinner numGPUPerNetSpinner;
	private Spinner numGPUSpinner;
	private Spinner numWorkersSpinner;
	private Button initSplitsCheck;
	private Button debugCheck;

	@Override
	public void createControl(Composite parent) {
		Composite con = new Composite(parent, SWT.BORDER);
		con.setLayout(new GridLayout(2,false));
		setControl(con);
		
		numGPUPerNetSpinner = createSpinner(con, "GPUs per one network", 1, 16);
		numGPUSpinner = createSpinner(con, "GPUs to use", 1, 16);
		numWorkersSpinner = createSpinner(con, "Total number of workers", 1, 16);
		
		debugCheck = new Button(con, SWT.CHECK);
		debugCheck.setText("Launch in debug mode");
		GridDataFactory.swtDefaults().span(2,1).applyTo(debugCheck);
		
		initSplitsCheck = new Button(con, SWT.CHECK);
		initSplitsCheck.setText("Always initialize data splits from scratch");
		GridDataFactory.swtDefaults().span(2,1).applyTo(initSplitsCheck);
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String val = configuration.getAttribute(ITaskConstants.YAML_SETTINGS, (String)null);
			if (val != null) {
				launchConfiguration = YamlIO.loadAs(val, LaunchConfiguration.class);
				numGPUPerNetSpinner.setSelection(launchConfiguration.getGpusPerNet());
				numGPUSpinner.setSelection(launchConfiguration.getNumGpus());
				numWorkersSpinner.setSelection(launchConfiguration.getNumWorkers());
				
				debugCheck.setSelection(launchConfiguration.isDebug());
				initSplitsCheck.setSelection(launchConfiguration.isCleanSplits());
			}
		} catch (Exception e) {
			DSIDEUIPlugin.log(e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "Musket";
	}
	
	private Spinner createSpinner(Composite parent, String label, int min, int max) {
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText(label);
		GridDataFactory.swtDefaults().applyTo(lbl);
		
		Spinner spinner = new Spinner(parent, SWT.NONE);
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		GridDataFactory.swtDefaults().applyTo(spinner);
		return spinner;
	}

}
