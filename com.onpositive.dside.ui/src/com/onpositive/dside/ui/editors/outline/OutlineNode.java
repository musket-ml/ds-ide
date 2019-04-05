package com.onpositive.dside.ui.editors.outline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.beans.IPropertyView;

import com.onpositive.dside.ast.ASTElement;

public class OutlineNode {

	protected ASTElement element;
	protected OutlineNode parent;

	public OutlineNode(ASTElement element,OutlineNode parent) {
		super();
		this.element = element;
		this.parent=parent;
	}
	

	public Collection<? extends OutlineNode> getChildren() {
		AbstractType type = element.getType(); 
		IPropertyView propertiesView = type.toPropertiesView();
		ArrayList<PropertyNode>props=new ArrayList<>();
		for (org.aml.typesystem.beans.IProperty p : propertiesView.properties()) {
			if (p.range().isArray()) {
				AbstractType componentType = p.range().componentType();
				if (componentType.isObject() && !componentType.propertySet().isEmpty()) {
					PropertyNode pn=new PropertyNode(p, this);
					props.add(pn);
				}
			}
		}
		if (props.size()>1) {
			Collections.sort(props);
			return props;
		}
		if (props.size()==1) {
			return props.get(0).getChildren();
		}
		return Collections.emptySet();
	}

	public Object getParent() {
		return this.parent;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutlineNode other = (OutlineNode) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return this.element.toString();
	}


	public int getStart() {
		return this.element.getStartOffset();
	}


	public int getEnd() {
		return this.element.getEndOffset();
	}
}
