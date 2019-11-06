package com.onpositive.musket_core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.onpositive.dside.tasks.IServerTask;
import com.onpositive.dside.tasks.ITaskConstants;
import com.onpositive.dside.ui.DSIDEUIPlugin;
import com.onpositive.dside.ui.LaunchConfiguration;
import com.onpositive.yamledit.io.YamlIO;

public class PublicMusketLaunchDelegate extends MusketLaunchConfigurationDelegate {

	protected IServerTask<?> obtainTask(ILaunchConfiguration conf) {
		IServerTask<?> task = super.obtainTask(conf);
		if (task != null) {
			return task;
		}
		try {
			String value = conf.getAttribute(ITaskConstants.YAML_SETTINGS,"");
			LaunchConfiguration loaded = YamlIO.loadAs(value, LaunchConfiguration.class);
			return loaded;
		} catch (CoreException e) {
			DSIDEUIPlugin.log(e);
		}
		return null;
	}

	
}
