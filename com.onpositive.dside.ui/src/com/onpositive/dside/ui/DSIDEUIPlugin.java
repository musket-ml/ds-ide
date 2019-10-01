package com.onpositive.dside.ui;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DSIDEUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.onpositive.dside.ui"; //$NON-NLS-1$

	// The shared instance
	private static DSIDEUIPlugin plugin;

	
	/**
	 * The constructor
	 */
	public DSIDEUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//getServer();
		plugin = this;
	}

	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DSIDEUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static void log(Exception exception) {
		log(IStatus.ERROR,"",exception);
	}
	
	public static void log(String message) {
		log(IStatus.ERROR,message,null);
	}
	
	public static void log(int severity, String message) {
		log(severity,message,null);
	}
	
	public static void log(int severity, String message, Exception exception) {
		getDefault().getLog().log(new Status(severity,PLUGIN_ID,message, exception));
	}
}
