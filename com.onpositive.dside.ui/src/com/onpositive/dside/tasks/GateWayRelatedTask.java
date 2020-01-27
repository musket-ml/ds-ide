package com.onpositive.dside.tasks;

import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.swt.widgets.Display;

import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.musket_core.IServer;
import com.onpositive.yamledit.io.YamlIO;

import py4j.GatewayServer;

public class GateWayRelatedTask extends PrivateServerTask<Object> {

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
	private com.onpositive.musket_core.IMusketProject musketProject;
	private ILaunch launch;
	
	private CompletableFuture<IServer> serverFuture = new CompletableFuture<IServer>();
	
	public CompletableFuture<IServer> getServer() {
		return serverFuture;
	}

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
		com.onpositive.musket_core.IMusketProject project2 = server.project(this.project.getLocation().toOSString());
		this.musketProject=project2;
		delegate.started(this);
		
		serverFuture.complete(musketServer);
	}
	
	public <T,R> void perform(T data,Class<R>resultClass,Consumer<R>func,Consumer<Throwable>error){
		if (launch.isTerminated()) {
			return;
		}
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Object taskResult = musketServer.performTask(YamlIO.dump(data), null);
					if (func != null) {
						if (resultClass.isInstance(taskResult)) {
							Display.getDefault().asyncExec(() -> func.accept(resultClass.cast(taskResult)));
						} else if (taskResult != null) {
							R loadAs = YamlIO.loadAs(new StringReader((String) taskResult), resultClass);
							Display.getDefault().asyncExec(() -> func.accept(loadAs));
						}
					}
				} catch (Exception e) {
					if (error != null) {
						error.accept(e);
					}
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
	public IProject[] getProjects() {
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
			DSIDEUIPlugin.log(e);
		}
	}
	
	public boolean isTerminated() {
		if (launch != null) {
			return launch.isTerminated();
		}
		return false;
	}
}
