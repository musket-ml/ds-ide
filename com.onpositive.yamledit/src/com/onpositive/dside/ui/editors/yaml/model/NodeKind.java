package com.onpositive.dside.ui.editors.yaml.model;

import java.util.Collections;
import java.util.Map;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider;

public class NodeKind implements IPropertyProvider {

	INodeListener listener;
	protected NodeType nodetype;
	

	public NodeKind(String kind, INodeListener listener) {
		super();
		this.listener = listener;
		this.nodetype = Registry.INSTANCE.getType(kind);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Iterable<IProperty> getProperties(Object arg0) {
		return Collections.emptyList();
	}

	@Override
	public IProperty getProperty(Object arg0, String arg1) {
		if (arg0 instanceof Class) {
			return null;
		}
		ModelNode node = (ModelNode) arg0;
		if (node==null) {
			return null;
		}
		if (arg1==null) {
			return null;
		}
		if (arg1.equals("children")) {
			return JavaPropertyProvider.instance.getProperty(arg0, "Children");
		}
		if (node.object instanceof Map) {
			return new MapProperty(this, arg1);
		}
		return null;
	}

	public boolean hasChildren() {
		return nodetype.chidren();
	}

	public NodeKind createChild(Object value) {
		String childrenKind = nodetype.getChildrenKind();
		return new NodeKind(childrenKind, listener);
	}

}
