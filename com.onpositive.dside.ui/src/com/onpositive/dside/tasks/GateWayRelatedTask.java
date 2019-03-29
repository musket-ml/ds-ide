package com.onpositive.dside.tasks;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;

import com.onpositive.musket_core.IServer;

import py4j.GatewayServer;

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
		com.onpositive.musket_core.IProject project2 = server.project(this.project.getLocation().toOSString());
		delegate.started(server, project2);
	}

	public void afterStart(ILaunch launch) {

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
}
