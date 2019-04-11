package com.onpositive.dside.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.BuiltIns;
import org.aml.typesystem.ITypeRegistry;
import org.aml.typesystem.beans.IProperty;
import org.aml.typesystem.beans.IPropertyView;
import org.aml.typesystem.meta.IHasType;
import org.aml.typesystem.meta.facets.HasKey;
import org.aml.typesystem.meta.facets.IsRef;
import org.aml.typesystem.meta.restrictions.DefaultPropertyMeta;
import org.aml.typesystem.values.ArrayImpl;
import org.aml.typesystem.values.IArray;
import org.aml.typesystem.values.IKnowsPropertyCount;
import org.aml.typesystem.values.IObject;
import org.aml.typesystem.values.ITypedObject;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class ASTElement implements IObject, ITypedObject, IHasLocation,IKnowsPropertyCount {

	protected Node node;
	
	
	
	protected AbstractType type;
	protected String key;
	protected ASTElement parent;
	protected ITypeRegistry registry;
	protected String oneValue;
	protected boolean fromNull = false;
	protected int num=-1;

	public ASTElement(Node node, AbstractType type, ASTElement astElement) {
		super();
		this.node = node;
		this.type = type;
//		if (this.type.name().startsWith("Data")) {
//			System.out.println("A");
//		}
		
		this.parent = astElement;
		
		if (type.name().equals("GenericDeclaration")) {
			if (node instanceof SequenceNode) {
				this.oneValue = "body";
			}
			try {
				Object property = this.getProperty("body");
				if (property instanceof IArray) {
					Object item = ((IArray) property).item(0);
					if (item instanceof ITypedObject) {
						ITypedObject t = (ITypedObject) item;
						AbstractType range = t.getType();
						Set<AbstractType> allSuperTypes = range.allSuperTypes();
						for (AbstractType ta : allSuperTypes) {
							if (ta.name().equals("Layer")) {
								this.type = getRegistry().getType("NetworkBlockDeclaration");
								this.oneValue = null;
								return;
							}
						}
					}
				}
				this.type = getRegistry().getType("PreprocessorDeclaration");
			} finally {
				this.oneValue = null;
			}
		}
	}
	
	

	public int getStartOffset() {
		if (this.node == null) {
			return 0;
		}
		return node.getStartMark().getIndex();
	}

	public int getEndOffset() {
		if (this.node == null) {
			return 0;
		}
		return node.getEndMark().getIndex();
	}

	public NodeTuple findInKey(String key) {
		if (this.node instanceof MappingNode) {
			MappingNode ms = (MappingNode) this.node;
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
	public Object getProperty(String name) {
		
		IPropertyView propertiesView = this.type.toPropertiesView();
		
		IProperty property = propertiesView.property(name);

		if (property == null) {
			return null;
		}
		
		if (name.equals(this.oneValue)) {
			AbstractType range = property.range();
			return parseValue(range, this.node, false);
		}
		if (this.fromNull) {
			return null;
		}

		AbstractType range = property.range();
		if (this.node instanceof ScalarNode) {
			if (range.isScalar()||range.equals(BuiltIns.ANY)) {
				List<IProperty> positionalProperties = propertiesView.positionalProperties();
				if (!positionalProperties.isEmpty()) {
					if (positionalProperties.get(0).id().equals(property.id())) {
						Node node2 = this.node;
						return parseValue(range, node2, false);
					}
				}
			}
			return null;
		}
		if (this.node instanceof SequenceNode) {
			DefaultPropertyMeta mmm = this.type.oneMeta(DefaultPropertyMeta.class);
			if (mmm!=null) {
				if (mmm.value().equals(property.id())) {
					return parseValue(range, node, false);	
				}
				else {
					return null;
				}
			}
			SequenceNode seq = (SequenceNode) this.node;
			List<IProperty> positionalProperties = propertiesView.positionalProperties();
			List<Node> value = seq.getValue();
			int indexOf = positionalProperties.indexOf(property);
			if (indexOf > -1 && indexOf < value.size()) {
				Node node2 = value.get(indexOf);
				return parseValue(range, node2, false);
			}
			return null;
		}
		if (this.node instanceof MappingNode) {
			MappingNode seq = (MappingNode) this.node;
			for (NodeTuple t : seq.getValue()) {
				if (t.getKeyNode() instanceof ScalarNode) {
					ScalarNode sc = (ScalarNode) t.getKeyNode();
					if (sc.getValue().equals(name)) {
						Object parseValue = parseValue(range, t.getValueNode(), false);
						AutoConvertToArray oneMeta = property.range().oneMeta(AutoConvertToArray.class);
						if (oneMeta!=null) {
							if (!(parseValue instanceof IArray)&&parseValue!=null) {
								ArrayImpl arr=new ArrayImpl();
								arr.add(parseValue);
								return arr;
							}
						}
						return parseValue;
					}
				}
			}
			for (NodeTuple t : seq.getValue()) {
				if (t.getKeyNode() instanceof ScalarNode) {
					ScalarNode sc = (ScalarNode) t.getKeyNode();

					if (sc.getValue().equals("args")) {
						return new ASTElement(t.getValueNode(), this.type, this).getProperty(name);
					}
				}
			}
			if (range.isObject()&&range.oneMeta(IsRef.class)!=null) {
				Object parseValue = parseValue(range, seq, false);
				return parseValue;
			}
		}
		return null;
	}

	private Object parseValue(AbstractType range, Node node, boolean ref) {
		if (range == null) {
			range = BuiltIns.STRING;
		}
		if (node instanceof ScalarNode) {
			ScalarNode s = (ScalarNode) node;
			String value = s.getValue();
			if (range == null) {
				return value;
			}
			if (value.equals("") && !ref) {
				return null;
			}
			if (range.isBoolean()&&(value.equals("true")||value.equals("false"))) {
				return Boolean.parseBoolean(value);
			}
			if (range.isInteger()) {
				try {
					return Integer.parseInt(value);
				} catch (NumberFormatException e) {
					return value;
				}
			}
			if (range.isNumber()) {
				try {
					return Double.parseDouble(value);
				} catch (NumberFormatException e) {
					return value;
				}
			}
			if (range.isObject()) {
				AbstractType type2 = getRegistry().getType(value);
				if (type2 != null) {
					ASTElement astElement = new ASTElement(node, type2, this);
					astElement.fromNull = true;
					return astElement;
				}

			}
			if (range.isArray() && range.oneMeta(IsRef.class) != null) {
				AbstractType type2 = getRegistry().getType(value);
				if (type2 != null) {
					ASTElement astElement = new ASTElement(node, type2, this);
					astElement.fromNull = true;
					ArrayImpl m = new ArrayImpl();
					m.add(astElement);
					return m;
				} else {
					ErrorElement errorElement = new ErrorElement(node, range, this);
					errorElement.setMessage("Can not resolve " + value + " to " + range.componentType().name());
					return errorElement;
				}
			}
			if (ref) {
				return new ASTElement(node, range, this);
			}
			return value;
		}
		if (node instanceof SequenceNode) {
			SequenceNode seq = (SequenceNode) node;
			if (range.isArray()) {
				List<Node> value = seq.getValue();
				AbstractType r1 = range;
				List<Object> collect = value.stream().map(x -> parseInSequnce(r1, x)).collect(Collectors.toList());
				for (int i=0;i<collect.size();i++) {
					Object object = collect.get(i);
					if (object instanceof ASTElement) {
						ASTElement el=(ASTElement) object;
						if (el.key==null) {
							el.num=i;
						}
					}
				}
				return new ArrayImpl(collect);
			} else {
				if (range.isObject()) {
					DefaultPropertyMeta oneMeta = range.oneMeta(DefaultPropertyMeta.class);
					if (oneMeta != null) {
						ASTElement astElement = new ASTElement(seq, range, this);
						astElement.oneValue = oneMeta.value();
						return astElement;
					}
					ASTElement astElement = new ASTElement(seq, range, this);
					return astElement;
					// List<IProperty> positionalProperties =
					// range.toPropertiesView().positionalProperties();

				}
				if (range.equals(BuiltIns.ANY)||range.isAnonimous()&&range.superType().equals(BuiltIns.ANY)) {
					return new ASTElement(node, range, this);
				}
				return new ErrorElement(node, range, this);
			}
		}
		if (node instanceof MappingNode) {
			MappingNode ms = (MappingNode) node;
			if (range.isObject()) {
				if (range.oneMeta(IsRef.class) != null) {
					ArrayList<Object> s = new ArrayList<>();
					if (ms.getValue().size() == 1) {
						for (NodeTuple t : ms.getValue()) {
							Node keyNode = t.getKeyNode();

							if (keyNode instanceof ScalarNode) {
								AbstractType componentType = range;
								ScalarNode sc = (ScalarNode) keyNode;
								String value2 = sc.getValue();
								boolean isRef = false;
								if (!ref) {
									IsRef oneMeta = range.oneMeta(IsRef.class);
									if (oneMeta != null) {
										isRef = true;
										componentType = getRegistry().getType(value2);
										if (componentType == null) {
											ErrorElement r = new ErrorElement(sc, range, this);
											r.setMessage("Can not resolve " + sc.getValue() + " to "
													+ range);
											return r;
										}
									}
								}

								Object parseValue = parseValue(componentType, t.getValueNode(), isRef);
								if (parseValue instanceof ASTElement) {
									ASTElement mm = (ASTElement) parseValue;
									mm.key = value2;
								}
								s.add(parseValue);
							}
						}
					}
					return s.get(0);

				}
				
				ASTElement astElement = new ASTElement(node, range, this);
				DefaultPropertyMeta oneMeta = range.oneMeta(DefaultPropertyMeta.class);
				if (oneMeta!=null) {
					//astElement.oneValue=oneMeta.value();
				}
				return astElement;
			}
			if (range.isArray() && range.componentType().isObject()) {

				List<NodeTuple> value = ms.getValue();
				ArrayList<Object> s = new ArrayList<>();
				if (range.oneMeta(HasKey.class) != null) {
					for (NodeTuple t : value) {
						Node keyNode = t.getKeyNode();

						if (keyNode instanceof ScalarNode) {
							AbstractType componentType = range.componentType();
							ScalarNode sc = (ScalarNode) keyNode;
							String value2 = sc.getValue();
							boolean isRef = false;
							if (!ref) {
								IsRef oneMeta = range.oneMeta(IsRef.class);
								if (oneMeta != null) {
									isRef = true;
									componentType = getRegistry().getType(value2);
									if (componentType == null) {
										ErrorElement r = new ErrorElement(sc, range, this);
										r.setMessage(
												"Can not resolve " + sc.getValue() + " to " + range.componentType());
										return r;
									}
								}
							}

							Object parseValue = parseValue(componentType, t.getValueNode(), isRef);
							if (parseValue instanceof ASTElement) {
								ASTElement mm = (ASTElement) parseValue;
								mm.key = value2;
							}
							s.add(parseValue);
						}
					}

					return new ArrayImpl(s);
				}
			}
		}
		return new ErrorElement(node, range, this);
	}

	private Object parseInSequnce(AbstractType range, Node x) {
		AbstractType componentType = range.componentType();
		if (componentType == null) {
			componentType = BuiltIns.STRING;
		}

		if (x instanceof MappingNode) {
			MappingNode ms = (MappingNode) x;
			List<NodeTuple> value = ms.getValue();
			if (componentType.isObject() && range.hasDirectMeta(HasKey.class)) {
				NodeTuple nodeTuple = value.get(0);
				String key = null;
				boolean isRef = false;
				Node keyNode = nodeTuple.getKeyNode();
				if (keyNode instanceof ScalarNode) {
					key = ((ScalarNode) keyNode).getValue();
					if (range.oneMeta(IsRef.class) != null) {
						componentType = getRegistry().getType(key);
						isRef = true;
					}
					if (componentType == null) {
						ErrorElement errorElement = new ErrorElement(keyNode, componentType, this);
						errorElement.setMessage("Can not resolve " + key);
						return errorElement;
					}
				}

				Object parseValue = parseValue(componentType, nodeTuple.getValueNode(), isRef);
				if (parseValue instanceof ASTElement) {
					ASTElement el = (ASTElement) parseValue;

					el.key = key;

					return el;
				}
				if (parseValue instanceof IHasLocation) {
					return parseValue;
				} else {
					if (parseValue == null || parseValue.equals("")) {
						return new ASTElement(nodeTuple.getKeyNode(), componentType, this);
					}
				}
			}
		}
		return parseValue(range.componentType(), x, false);
	}

	@Override
	public Set<String> keys() {
		if (node instanceof MappingNode) {
			List<NodeTuple> value = ((MappingNode) node).getValue();
			LinkedHashSet<String> keys = new LinkedHashSet<>();
			for (NodeTuple t : value) {
				Node keyNode = t.getKeyNode();
				if (keyNode instanceof ScalarNode) {
					ScalarNode sc = (ScalarNode) keyNode;
					keys.add(sc.getValue());
				}
			}
			DefaultPropertyMeta oneMeta = this.type.oneMeta(DefaultPropertyMeta.class);
			if (oneMeta!=null&&!keys.contains(oneMeta.value())) {
				
				return Collections.emptySet();
			}
			return keys;
		}
		return Collections.emptySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		ASTElement other = (ASTElement) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

	public ASTElement getParent() {
		return parent;
	}

	public ITypeRegistry getRegistry() {
		if (this.registry == null && this.parent != null) {
			return this.parent.getRegistry();
		}
		return registry;
	}

	public void setRegistry(ITypeRegistry registry) {
		this.registry = registry;
	}

	@Override
	public AbstractType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		if (this.key!=null) {
			return this.key;
		}
		if (this.node instanceof ScalarNode) {
			ScalarNode c = (ScalarNode) this.node;
			return c.getValue();
		}
		
		if (this.num!=-1) {
			return ""+this.num;
		}		
		return super.toString();
	}

	@Override
	public int propertyCount() {
		if (this.node instanceof SequenceNode) {
			SequenceNode s=(SequenceNode)this.node;
			DefaultPropertyMeta mmm = this.type.oneMeta(DefaultPropertyMeta.class);
			if (mmm!=null) {
				return 1;				
			}
			return s.getValue().size();
		}
		if (this.node instanceof MappingNode) {
			MappingNode s=(MappingNode)this.node;
			for (NodeTuple t:s.getValue()) {
				if (t.getKeyNode()instanceof ScalarNode) {
					ScalarNode sa=(ScalarNode) t.getKeyNode();
					if (sa.getValue().equals("args")) {
						return new ASTElement(t.getValueNode(), type, parent).propertyCount();
					}
				}
			}
			return s.getValue().size();
		}
		if (this.node instanceof ScalarNode) {
			ScalarNode s=(ScalarNode)this.node;
			if (s.getValue()==null||s.getValue().trim().length()==0) {
				return 0;
			}
		}
		if (!this.fromNull) {
			return 1;
		}
		
		return 0;
	}



	public ASTElement getRoot() {
		if (this.parent!=null) {
			return this.parent.getRoot();
		}
		return this;
	}
}
