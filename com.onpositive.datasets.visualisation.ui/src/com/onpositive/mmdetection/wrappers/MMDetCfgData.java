package com.onpositive.mmdetection.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.onpositive.semantic.model.api.labels.ILabelProvider;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.IHasMeta;

public class MMDetCfgData {
	
	public MMDetCfgData(String architecture, String path, String wsPath) {
		super();
		this.architecture = architecture;
		this.path = path;
		this.wsPath = wsPath;
	}

	private String architecture;
	
	private String path;
	
	private String wsPath;
	
	public String getArchitecture() {
		return architecture;
	}

	public String getPath() {
		return path;
	}
	
	public String getPathNoEdit() {
		return path;
	}
	
	public void setPathNoEdit(String val) {}

	public String getWSPath() {
		return wsPath;
	}

	private HashMap<String,String> parameters = new HashMap<String, String>();
	
	public void addParameter(String paramName, String paramValue) {
		this.parameters.put(paramName, paramValue);
	}
	
	public String getParameterValue(String paramName) {
		return this.parameters.get(paramName);
	}
	
	public List<String> paramNames(){
		return new ArrayList<String>(parameters.keySet());
	}
	
//	public static class PathLabelProvider implements ITextLabelProvider {
//
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 3419755403469303451L;
//
//		@Override
//		public String getDescription(Object arg0) {
//			return null;
//		}
//
//		@Override
//		public String getText(IHasMeta arg0, Object arg1, Object arg2) {			
//			return "ggg";
//		}
//		
//	}
}
