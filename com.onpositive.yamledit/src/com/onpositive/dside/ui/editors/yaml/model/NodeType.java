package com.onpositive.dside.ui.editors.yaml.model;

import java.util.LinkedHashMap;
import java.util.Map;


public class NodeType {

	protected Map<String,PropertyDescription>properties=new LinkedHashMap<>();
	protected boolean children;
	protected String childrenKind;
	protected String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getChildrenKind() {
		return childrenKind;
	}

	public void setChildrenKind(String childrenKind) {
		this.childrenKind = childrenKind;
	}

	public boolean isChildren() {
		return children;
	}

	public void setChildren(boolean children) {
		this.children = children;
	}

	public Map<String, PropertyDescription> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PropertyDescription> properties) {
		this.properties = properties;
	}

	public boolean chidren() {
		return children;
	}

}
