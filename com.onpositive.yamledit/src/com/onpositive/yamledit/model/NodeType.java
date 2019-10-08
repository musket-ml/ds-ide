package com.onpositive.yamledit.model;

import java.util.LinkedHashMap;
import java.util.Map;


public class NodeType {

	protected Map<String,PropertyDescription>properties=new LinkedHashMap<>();
	protected boolean children;
	protected String childrenKind;
	protected String icon;
	protected String polimophic;
	protected boolean closed;
	protected Map<String,NodeType>nodeTypes=new LinkedHashMap<String, NodeType>();
	protected String defaultProperty;
	protected String type;
	protected boolean root;
	protected String customValidator;
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

	public Map<String, NodeType> getTypes() {
		return nodeTypes;
	}

	public void setTypes(Map<String, NodeType> nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

	public String getPolimophic() {
		return polimophic;
	}

	public void setPolimophic(String polimophic) {
		this.polimophic = polimophic;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getDefaultProperty() {
		return defaultProperty;
	}

	public void setDefaultProperty(String defaultProperty) {
		this.defaultProperty = defaultProperty;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public String getCustomValidator() {
		return customValidator;
	}

	public void setCustomValidator(String customValidator) {
		this.customValidator = customValidator;
	}

}
