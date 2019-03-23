package com.onpositive.musket_core;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class ProgressMonitorWrapper implements IProgressReporter{

	protected IProgressMonitor monitor;
	private IOConsoleOutputStream output;
	private IOConsoleOutputStream error;
	
	
	public ProgressMonitorWrapper(IProgressMonitor monitor, IOConsoleOutputStream newOutputStream, IOConsoleOutputStream errorOutputStream) {
		super();
		this.monitor = monitor;
		this.output=newOutputStream;
		this.error=errorOutputStream;
	}

	@Override
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	@Override
	public void task(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	@Override
	public boolean worked(int worked) {
		monitor.worked(worked);
		return monitor.isCanceled();
	}
	
	public void error(String message) {
		monitor.subTask(message);
		
	}
	public void stdout(String message) {
		try {
			output.write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void stderr(String message) {
		try {
			error.write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void done() {
		monitor.done();
	}

}
