package com.onpositive.musket_core;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobFunction;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.yaml.snakeyaml.Yaml;

import com.onpositive.dside.dto.PythonError;
import com.onpositive.dside.dto.introspection.InstrospectedFeature;
import com.onpositive.dside.dto.introspection.InstrospectionResult;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class ProjectWrapper {

	public ProjectWrapper(String projectPath) {
		this.path = projectPath;
		String absolutePath = projectMetaPath();
		if (new File(absolutePath).exists()) {
			FileReader fileReader;
			try {
				fileReader = new FileReader(absolutePath);
				try {
					InstrospectionResult loadAs = new Yaml().loadAs(fileReader, InstrospectionResult.class);
					this.refreshed(loadAs);
					}finally {
						fileReader.close();
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	protected String path;

	protected ArrayList<Runnable> requests = new ArrayList<>();

	private InstrospectionResult details = new InstrospectionResult();

	public InstrospectionResult getDetails() {
		return details;
	}

	public void setDetails(InstrospectionResult details) {
		this.details = details;
	}

	public synchronized void refresh(Runnable r) {
		this.requests.add(r);
		Job create = Job.create("Refreshing project meta", new IJobFunction() {
			
			@Override
			public IStatus run(IProgressMonitor monitor) {
				String absolutePath = projectMetaPath();
				ProcessBuilder command = new ProcessBuilder().command("python", "-m", "musket_core.inspectProject", "--project",
						path, "--out", absolutePath);
				try {
					command.environment().putAll(System.getenv());
					File file = new File(getMetaDir() + "/error.log");
					command.redirectError(file);
					command.redirectOutput(new File(getMetaDir() + "/output.log"));
					int waitFor = command.start().waitFor();
					if (waitFor!=0) {
						List<String> readAllLines = Files.readAllLines(file.toPath());
						PythonError pythonError = new PythonError(readAllLines);
						Display.getDefault().asyncExec(new Runnable() {
							
							@Override
							public void run() {
								boolean createObject = WidgetRegistry.createObject(new StackVisualizer(pythonError));
								if (createObject) {
									pythonError.open();
								}
							}
						});
						return Status.OK_STATUS;						
					}
					FileReader fileReader = new FileReader(absolutePath);
					try {
					InstrospectionResult loadAs = new Yaml().loadAs(fileReader, InstrospectionResult.class);
					refreshed(loadAs);
					
					}finally {
						fileReader.close();
					}
					// ServerManager.perform(new IntrospectTask(this));
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		});
		create.schedule();

	}

	private String projectMetaPath() {
		return new File(this.getMetaDir(), "meta.yaml").getAbsolutePath();
	}

	private File getMetaDir() {
		File file = new File(this.path,".meta");
		file.mkdirs();
		return file;
	}

	protected synchronized void refreshed(InstrospectionResult details) {
		try {
			if (details == null) {
				return;
			}
			this.details = details;
			for (Runnable r : requests) {
				r.run();
			}
		} finally {
			requests.clear();
		}
	} 

	public List<InstrospectedFeature> getTasks() {
		return details.getFeatures().stream().filter(x -> x.getKind().equals("task")).collect(Collectors.toList());
	}
}
