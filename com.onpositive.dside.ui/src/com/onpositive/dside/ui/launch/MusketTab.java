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
	private Button fitFromScratch;
	private Button allowResume;
	private Button onlyReports;

	@Override
	public void createControl(Composite parent) {
		Composite con = new Composite(parent, SWT.BORDER);
		con.setLayout(new GridLayout(2,false));
		setControl(con);
		
		numGPUPerNetSpinner = createSpinner(con, "GPUs per one network", 1, 16);
		numGPUSpinner = createSpinner(con, "GPUs to use", 1, 16);
		numWorkersSpinner = createSpinner(con, "Total number of workers", 1, 16);
		
		fitFromScratch = createButton(con, "Fit from start", SWT.RADIO);
	 	allowResume = createButton(con, "Resume incomplete experiments", SWT.RADIO);
	 	onlyReports = createButton(con, "Only generate reports", SWT.RADIO);
		
		debugCheck = createButton(con, "Launch in debug mode", SWT.CHECK);
		initSplitsCheck = createButton(con, "Always initialize data splits from scratch", SWT.CHECK);
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		if (launchConfiguration == null) {
			launchConfiguration = new LaunchConfiguration();
		}
		launchConfiguration.setNumGpus(1);
		launchConfiguration.setNumWorkers(1);
		launchConfiguration.setGpusPerNet(1);
		
		launchConfiguration.setAllowResume(true);
		launchConfiguration.setOnlyReports(false);
		launchConfiguration.setFitFromScratch(false);
		
		launchConfiguration.setDebug(false);
		launchConfiguration.setCleanSplits(true);
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
				
				allowResume.setSelection(launchConfiguration.isAllowResume());
				onlyReports.setSelection(launchConfiguration.isOnlyReports());
				fitFromScratch.setSelection(launchConfiguration.isFitFromScratch());
				
				debugCheck.setSelection(launchConfiguration.isDebug());
				initSplitsCheck.setSelection(launchConfiguration.isCleanSplits());
			} else {
				launchConfiguration = new LaunchConfiguration();
			}
		} catch (Exception e) {
			DSIDEUIPlugin.log(e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (launchConfiguration == null) {
			launchConfiguration = new LaunchConfiguration();
		}

		launchConfiguration.setNumGpus(numGPUSpinner.getSelection());
		launchConfiguration.setNumWorkers(numWorkersSpinner.getSelection());
		launchConfiguration.setGpusPerNet(numGPUPerNetSpinner.getSelection());
		
		launchConfiguration.setAllowResume(allowResume.getSelection());
		launchConfiguration.setOnlyReports(onlyReports.getSelection());
		launchConfiguration.setFitFromScratch(fitFromScratch.getSelection());
		
		launchConfiguration.setDebug(debugCheck.getSelection());
		launchConfiguration.setCleanSplits(initSplitsCheck.getSelection());
		
		doSaveLaunchCfg(launchConfiguration, configuration);
		
	}

	private void doSaveLaunchCfg(LaunchConfiguration from, ILaunchConfigurationWorkingCopy to) {
		String dump = YamlIO.dump(from);
		to.setAttribute(ITaskConstants.YAML_SETTINGS, dump);
	}

	@Override
	public String getName() {
		return "Musket";
	}
	
	private Button createButton(Composite parent, String label, int style) {
		Button btn = new Button(parent, style);
		btn.setText(label);
		GridDataFactory.swtDefaults().span(2,1).applyTo(btn);
		return btn;
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
