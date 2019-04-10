package com.onpositive.dside.tasks;

import java.io.StringReader;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.swt.widgets.Display;
import org.yaml.snakeyaml.Yaml;

import com.onpositive.musket_core.IProgressReporter;
import com.onpositive.musket_core.IServer;

import py4j.GatewayServer;
import py4j.Py4JException;

public class GateWayRelatedTask implements IServerTask<Object> {

	protected IProject project;
	private IGateWayServerTaskDelegate delegate;

	public GateWayRelatedTask(IProject project, IGateWayServerTaskDelegate delegate) {
		super();
		this.project = project;
		this.delegate = delegate;
	}

	protected boolean debug;
	private int listeningPort;
	private GatewayServer server;
	protected IServer musketServer;
	private com.onpositive.musket_core.IProject musketProject;
	private ILaunch launch;

	@Override
	public Class<Object> resultClass() {
		return Object.class;
	}

	@Override
	public void afterCompletion(Object taskResult) {
		try {
			delegate.terminated();
		} finally {
			server.shutdown();
		}
	}

	@Override
	public void beforeStart() {
		GatewayServer.turnLoggingOff();
		server = new GatewayServer(this, 0);
		server.start();
		listeningPort = server.getListeningPort();
	}

	public void created(IServer server) {
		this.musketServer=server;		
		com.onpositive.musket_core.IProject project2 = server.project(this.project.getLocation().toOSString());
		this.musketProject=project2;
		delegate.started(this);		
	}
	
	public <T,R> void perform(T data,Class<R>resultClass,Consumer<R>func,Consumer<Throwable>error){
		if (launch.isTerminated()) {
			
		}
		Thread thread = new Thread() {
			@Override
			public void run() {
				Yaml yaml = new Yaml();
				try {
				Object performTask = musketServer.performTask(yaml.dump(data), null);
				if (resultClass.isInstance(performTask)){
					Display.getDefault().asyncExec(()->func.accept(resultClass.cast(performTask)));
				}
				else {
					R loadAs = yaml.loadAs(new StringReader((String) performTask), resultClass);
					Display.getDefault().asyncExec(()->func.accept(loadAs));
				}
				}catch (Exception e) {
					error.accept(e);
				}
			}			
		};
		thread.setDaemon(true);
		thread.start();			
	}

	public void afterStart(ILaunch launch) {
		this.launch=launch;
	};

	@Override
	public boolean isDebug() {
		return this.debug;
	}

	@Override
	public IProject[] getProject() {
		return new IProject[] { project };
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	public void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}

	@Override
	public String toString() {
		return "server";
	}

	public void terminate() {
		try {
			this.launch.terminate();
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}
}
