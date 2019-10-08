package com.onpositive.dside.ui.editors.outline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.aml.typesystem.beans.IProperty;
import org.aml.typesystem.values.IArray;

import com.onpositive.yamledit.ast.ASTElement;

public class PropertyNode extends OutlineNode implements Comparable<PropertyNode>{

	protected IProperty property;
	
	public PropertyNode(IProperty property, OutlineNode parent) {
		super(parent.element,parent);
		this.property = property;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyNode other = (PropertyNode) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	@Override
	public int compareTo(PropertyNode o) {
		return property.id().compareTo(o.property.id());
	}
	
	@Override
	public Collection<? extends OutlineNode> getChildren() {
		Object property2 = element.getProperty(this.property.id());
		if (property2 instanceof IArray) {
			IArray array=(IArray) property2;
			ArrayList<OutlineNode>nodes=new ArrayList<>();
			for (int i=0;i<array.length();i++) {
				Object item = array.item(i);
				if (item instanceof ASTElement) {
					nodes.add(new OutlineNode((ASTElement) item, this));
				}
			}
			return nodes;
		}
		if (property2 instanceof ASTElement) {
			ASTElement el=(ASTElement)property2;
			return Collections.singletonList(new OutlineNode(el, this));
		}
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		return this.property.id();
	}
}
