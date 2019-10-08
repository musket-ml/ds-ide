package com.onpositive.yamledit.ast;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.values.IParseError;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;


public class ErrorElement implements IParseError,IHasLocation{

	protected final Node origin;
	protected final AbstractType property;
	protected final ASTElement parent;
	private String message;
	
	public ErrorElement(Node origin, AbstractType property, ASTElement parent) {
		super();
		this.origin = origin;
		this.property = property;
		this.parent = parent;
	}

	@Override
	public String getMessage() {
		if (message!=null) {
			return message;
		}
		if (property.isArray()&&!(origin instanceof SequenceNode)) {
			return "should be array";
		}
		if (!property.isArray()&&(origin instanceof SequenceNode)) {
			if (property.isObject()) {
				return "expected object but got array";
			}
			return "expected scalar";
		}
		return "Error";
	}

	public int getStartOffset() {
		return origin.getStartMark().getIndex();
	}

	public int getEndOffset() {
		return origin.getEndMark().getIndex();
	}

	public NodeTuple findInKey(String key) {
		if (this.origin instanceof MappingNode) {
			MappingNode ms = (MappingNode) this.origin;
			for (NodeTuple t : ms.getValue()) {
				if (t.getKeyNode() instanceof ScalarNode) {
					ScalarNode sc = (ScalarNode) t.getKeyNode();
					if (sc.getValue().equals(key)) {
						return t;
					}
				}
			}
		}
		return null;
	}

	@Override
	public IHasLocation getParent() {
		return parent;
	}

	public void setMessage(String message) {
		this.message=message;
	}
}
