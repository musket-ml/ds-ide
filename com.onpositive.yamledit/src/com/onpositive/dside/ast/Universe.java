package com.onpositive.dside.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.BuiltIns;
import org.aml.typesystem.ITypeRegistry;
import org.aml.typesystem.Status;
import org.aml.typesystem.TypeOps;
import org.aml.typesystem.TypeRegistryImpl;
import org.aml.typesystem.beans.IProperty;
import org.aml.typesystem.beans.IPropertyView;
import org.aml.typesystem.meta.facets.Description;
import org.aml.typesystem.meta.facets.HasKey;
import org.aml.typesystem.meta.facets.IsRef;
import org.aml.typesystem.meta.restrictions.DefaultPropertyMeta;
import org.aml.typesystem.meta.restrictions.minmax.MaxProperties;
import org.aml.typesystem.values.IArray;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import com.onpositive.dside.dto.introspection.InstrospectedFeature;
import com.onpositive.dside.dto.introspection.InstrospectionResult;
import com.onpositive.dside.dto.introspection.IntrospectedParameter;

public class Universe extends TypeRegistryImpl {

	private static final String COMMONS_FILE_NAME = "common.yaml";

	private final class InnerRegistry implements ITypeRegistry {
		private final HashMap<String, InstrospectedFeature> rs;
		HashMap<String, AbstractType> types = new HashMap<>();

		private InnerRegistry(HashMap<String, InstrospectedFeature> rs) {
			this.rs = rs;
		}

		@Override
		public Iterator<AbstractType> iterator() {
			return null;
		}

		@Override
		public Collection<AbstractType> types() {
			ArrayList<AbstractType>t=new ArrayList<>();
			for (String s:rs.keySet()) {
				t.add(getType(s));
			}
			return t;
		}

		@Override
		public AbstractType getType(String type) {
			
			if (type == null) {
				type = "string";
			}
			if (type.startsWith("if(")) {
				return getType("condition");
			}
			if (type.endsWith("[]")) {
				AbstractType type2 = getType(type.substring(0,type.length()-2));
				if (type2!=null) {
					return TypeOps.array(type2);
				}
			}
			if (types.containsKey(type)) {
				return types.get(type);
			}
			AbstractType type2 = Universe.this.getType(type);
			if (type2 != null) {
				return type2;
			}
			InstrospectedFeature instrospectedFeature = rs.get(type);

			if (instrospectedFeature != null) {
				AbstractType t = featureToType(this, instrospectedFeature);
				types.put(type, t);
				return t;
			}

			return null;
		}

		private AbstractType featureToType(ITypeRegistry iTypeRegistry, InstrospectedFeature instrospectedFeature) {
//			if (instrospectedFeature.getName().startsWith("split")) {
//				System.out.println("A");
//			}
			AbstractType superT = null;
			String kind = instrospectedFeature.getKind();
			if (kind == null) {
				kind = "LayerOrPreprocessor";
			}
			if (kind.equals("preprocessor")) {
				kind = "Preprocessor";
			}
			if (kind.equals("dataset_factory")) {
				kind = "DatasetFactory";
			}
			if (kind.equals("metric_or_loss")) {
				kind = "function";
			}
			if (kind.equals("model")) {
				kind = "Layer";
			}
			if (kind.equals("metrics")) {
				kind = "function";
			}
			if (kind.equals("losses")) {
				kind = "function";
			}
			if (types.containsKey(kind)) {
				superT = types.get(instrospectedFeature.getKind());
			} else {
				AbstractType type = Universe.this.getType(kind);
				if (type != null) {
					superT = type;
				} else {
					superT = TypeOps.derive(kind, BuiltIns.OBJECT);
					types.put(instrospectedFeature.getKind(), superT);
				}
			}
			AbstractType derive = TypeOps.derive(instrospectedFeature.getName(), superT);
			ArrayList<IntrospectedParameter> parameters = instrospectedFeature.getParameters();
			int pc = 0;
			String doc=null;
			if (instrospectedFeature.getDoc() != null && instrospectedFeature.getDoc().length() > 0) {
				doc = instrospectedFeature.getDoc();
				
				
			}
			else {
				doc=instrospectedFeature.getSource();
			}
			derive.addMeta(new Description(doc));
			if (instrospectedFeature.getSourcefile()!=null&&instrospectedFeature.getSourcefile().endsWith("merge.py")) {
				AbstractType declareProperty = derive.declareProperty("inputs", TypeOps.array(BuiltIns.STRING), false);
				derive.addMeta(new DefaultPropertyMeta("inputs"));
			}
			boolean lastLayers=false;
			if (!kind.equals("function") && !kind.equals("function")) {
				for (IntrospectedParameter p : parameters) {
					String type = p.getType();
					if (type != null) {
						if (type.equals("bool")) {
							type = BuiltIns.BOOLEAN.name();
						}
						if (type.equals("int")) {
							type = BuiltIns.INTEGER.name();
						}
						if (type.equals("str")) {
							type = BuiltIns.STRING.name();
						}
						if (type.equals("float")) {
							type = BuiltIns.NUMBER.name();
						}
					}
					else {
						type="any";
					}
					AbstractType type2 = getType(type);
					if (type.equals("Layer[]")) {
						lastLayers=true;
						type2.addMeta(new HasKey(true));
						type2.addMeta(new IsRef(true));
					}
					if (type.equals("Preprocessor[]")) {
						lastLayers=true;
						type2.addMeta(new HasKey(true));
						type2.addMeta(new IsRef(true));
					}
					if (p.getName().equals("layer")) {
						if (derive.name().equals("Bidirectional") || derive.name().equals("TimeDistributed")) {
							type = "Layer";
							type2 = TypeOps.array(getType("Layer"));
							type2.addMeta(new IsRef(true));
							type2.addMeta(new HasKey(true));
						}
					}
					if (p.getName().equals("input")||p.getName().equals("inp")) {
						if (derive.isSubTypeOf("Preprocessor") || derive.isSubTypeOf("Layer")) {
							continue;
						}
					}
					pc++;
					
					derive.declareProperty(p.getName(), type2, p.getDefaultValue() != null, true, p.getDefaultValue());
				}
			}
			if (pc==1&&lastLayers) {
				derive.addMeta(new DefaultPropertyMeta(instrospectedFeature.getParameters().get(0).getName()));
			}
			derive.addMeta(new MaxProperties(pc));
			derive.closeUnknownProperties();
			return derive;
		}
	}

	protected AbstractType root;

	public Universe(ITypeRegistry parent) {
		super(parent);
	}

	public AbstractType getRoot() {
		return root;
	}

	public void setRoot(AbstractType root) {
		this.root = root;
	}

	public Status validate(String content, InstrospectionResult instrospectionResult,File file) {
		ASTElement obj = buildRoot(content, instrospectionResult,file);
		Status validate = root.validate(obj);
		if (content.indexOf("hyperparameters:")!=-1) {
			return Status.OK_STATUS;
		}
		
		return validate;
	}

	public ASTElement buildRoot(String content, InstrospectionResult instrospectionResult,File inputFile) {
		HashMap<String, InstrospectedFeature> rs = new HashMap<>();
		
		
		instrospectionResult.getFeatures().forEach(v -> {
			rs.put(v.getName(), v);
			rs.put(v.getName().toLowerCase(), v);
			if (v.getKind().equals("metrics")||v.getKind().equals("metric_or_loss")) {
				rs.put("val_" + v.getName().toLowerCase(), v);
				rs.put("loss", v);
				rs.put("val_loss", v);
			}
			rs.put(v.getName().substring(0, 1).toLowerCase() + v.getName().substring(1), v);
		});
		Node compose = new Yaml().compose(new StringReader(content));
		injectCommons(inputFile, compose);
		ASTElement obj = new ASTElement((MappingNode) compose, root, null);
		obj.setRegistry(new InnerRegistry(rs));
		Object property = obj.getProperty("declarations");
		if (property instanceof IArray) {
			IArray arr = (IArray) property;
			for (int i = 0; i < arr.length(); i++) {
				Object item = arr.item(i);
				if (item instanceof ASTElement) {
					ASTElement as = (ASTElement) item;
					InstrospectedFeature rr = new InstrospectedFeature();
					rr.setName(as.key);
					rs.put(as.key, rr);
					Object property2 = as.getProperty("parameters");
					if (property2 != null) {
						//
						if (property2 instanceof IArray) {
							IArray r = (IArray) property2;
							for (int j = 0; j < r.length(); j++) {
								Object item2 = r.item(j);
								if (item2 instanceof String) {
									IntrospectedParameter e = new IntrospectedParameter();
									e.setName(item2.toString());
									e.setType("any");
									rr.getParameters().add(e);

								}
							}
						}
					}
					IntrospectedParameter introspectedParameter = new IntrospectedParameter();
					introspectedParameter.setName("name");
					introspectedParameter.setDefaultValue("");
					rr.getParameters().add(introspectedParameter);
					introspectedParameter = new IntrospectedParameter();
					introspectedParameter.setName("inputs");
					introspectedParameter.setType("any");
					introspectedParameter.setDefaultValue("");
					rr.getParameters().add(introspectedParameter);
				}
			}
		}
		return obj;
	}

	private void injectCommons(File inputFile, Node compose) {
		if (COMMONS_FILE_NAME.equals(inputFile.getName())) {
			return;
		}
		File file = new File(inputFile.getParent(),COMMONS_FILE_NAME);
		if(file.exists()) {
			try (FileReader fileReader = new FileReader(file)){
				Node root = new Yaml().compose(fileReader);
				if (root instanceof MappingNode) {
					merge(compose,(MappingNode) root);
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (Exception e) {
				// Best effort
			}
		}
	}

	private void merge(Node compose, MappingNode root2) {
		if (compose instanceof MappingNode) {
			MappingNode nm=(MappingNode) compose;
			for (NodeTuple toMerge:root2.getValue()) {
				if (toMerge.getKeyNode() instanceof ScalarNode) {
					ScalarNode c=(ScalarNode) toMerge.getKeyNode();
					if (!hasKey(nm, c.getValue())) {
						nm.getValue().add(toMerge);
					}
					else {
						if (c.getValue().equals("declarations")||c.getValue().equals("callbacks")||c.getValue().equals("datasets")) {
							Node key = getKey(nm, c.getValue());
							if (key instanceof MappingNode) {
								Node valueNode = toMerge.getValueNode();
								if (valueNode instanceof MappingNode) {
									merge((MappingNode)key,(MappingNode) valueNode);	
								}								
							}
						}
					}
				}
			}
		}
	}
	public boolean hasKey(MappingNode n,String key) {
		for (NodeTuple t:n.getValue()) {
			if (t.getKeyNode() instanceof ScalarNode) {
				ScalarNode sc=(ScalarNode)t.getKeyNode();
				if (sc.getValue().equals(key)) {
					return true;
				}
			}
		}
		return false;
	}
	public Node getKey(MappingNode n,String key) {
		for (NodeTuple t:n.getValue()) {
			if (t.getKeyNode() instanceof ScalarNode) {
				ScalarNode sc=(ScalarNode)t.getKeyNode();
				if (sc.getValue().equals(key)) {
					return t.getValueNode();
				}
			}
		}
		return null;
	}

	public static class CompletionSuggestions {
		public final ASTElement element;
		public Collection<AbstractType> values;
		public final boolean suggestProperties;

		public CompletionSuggestions(ASTElement element, boolean suggestProperties) {
			super();
			this.element = element;
			this.suggestProperties = suggestProperties;
		}
		public CompletionSuggestions(Collection<AbstractType>vals) {
			super();
			this.element = null;
			this.values=vals;
			this.suggestProperties =false;
		}
	}

	public CompletionSuggestions find(CompletionContext completionContext, InstrospectionResult details,File inputFile) {
		String content = completionContext.content;
		
		
		ASTElement buildRoot = buildRoot(content, details,inputFile);
		ArrayList<String> seq = completionContext.getSeq();
		if (seq.isEmpty()) {
			return new CompletionSuggestions(buildRoot, true);
		}
		int i = 0;
		IProperty property = null;
		
		while (i < seq.size()) {
			String string = seq.get(i);
			if (string.isEmpty()) {
				i++;
				continue;
			}
			if (property == null) {
				IPropertyView propertiesView = buildRoot.type.toPropertiesView();
				property = propertiesView.property(seq.get(i));
				if (property == null) {
					DefaultPropertyMeta oneMeta = buildRoot.getType().oneMeta(DefaultPropertyMeta.class);
					if (oneMeta!=null) {
						property=propertiesView.property(oneMeta.value());
						continue;
					}
					
					if (property==null) {
						return null;
					}
				}
				i++;
			}
			else {
				AbstractType range = property.range();
				boolean isKey=range.oneMeta(HasKey.class)!=null;
				if (range.isArray()) {
					range=range.componentType();
				}
				if (isKey) {
								
					Object property2 = buildRoot.getProperty(property.id());
					if (property2 instanceof IArray) {
						IArray ass=(IArray) property2;
						boolean found=false;
						for (int j=0;j<ass.length();j++) {
							Object item = ass.item(j);
							if (item instanceof ASTElement) {
								
								ASTElement item2 = (ASTElement)item;
								if (item2.key!=null&&item2.key.equals(seq.get(i))) {
									buildRoot=item2;
									property=null;
									found=true;
									i++;
									break;
								}
							}
							else {
								return null;
							}
						}
						if (!found) {
							return null;
						}
					}
				}
				else {
					Object property2 = buildRoot.getProperty(property.id());
					if (property2 instanceof IArray) {
						IArray ass=(IArray) property2;
						boolean found=false;
						for (int j=0;j<ass.length();j++) {
							Object item = ass.item(j);
							if (item instanceof ASTElement) {
								
								ASTElement item2 = (ASTElement)item;
								if (true) {
									buildRoot=item2;
									property=null;
									i++;
									found=true;
									break;
								}
							}
							else {
								return null;
							}
						}
						if (!found) {
							return null;
						}
					}
					else {
						return null;
						//System.out.println("A");
					}
				}
			}
		}
		if (buildRoot!=null) {
			if (buildRoot.oneValue!=null) {
				if (property==null) {
					property=buildRoot.getType().toPropertiesView().property(buildRoot.oneValue);
				}
			}
		}
		if (property!=null) {
			AbstractType range = property.range();
			if (range.isArray()) {
				range=range.componentType();
			}
			if (range.isObject()) {
				
			}
			ITypeRegistry registry = buildRoot.getRegistry();
			Collection<AbstractType> types = registry.types();
			ArrayList<AbstractType>filteredTypes=new ArrayList<>();
			for (AbstractType ca:types) {
				if (ca.isSubTypeOf(range)) {
					filteredTypes.add(ca);
				}
			}
			if (!filteredTypes.isEmpty()) {
				return new CompletionSuggestions(filteredTypes);
			}
			else {
				AbstractType range2 = property.range();
				NodeTuple findInKey = buildRoot.findInKey(property.id());
				if (findInKey!=null) {
					Node valueNode = findInKey.getValueNode();
					if (valueNode instanceof SequenceNode) {
						SequenceNode sequenceNode=(SequenceNode) valueNode;
						for (Node node:sequenceNode.getValue()) {
							if (node.getStartMark().getIndex()<completionContext.completionOffset) {
								if (node.getStartMark().getIndex()>completionContext.completionOffset) {
									return new CompletionSuggestions(new ASTElement(node, range2.componentType(), buildRoot), true);			
								}
							}
						}
						Boolean flowStyle=true;
						return new CompletionSuggestions(new ASTElement(new MappingNode(Tag.MAP, true, new ArrayList<>(), valueNode.getStartMark(), 
								valueNode.getEndMark(), flowStyle), range2.componentType(), buildRoot), true);
					}
					else {
						return new CompletionSuggestions(new ASTElement(valueNode, range2.componentType(), buildRoot), true);			
					}
				}
			}
		}	
		if (buildRoot!=null) {
			
			return new CompletionSuggestions(buildRoot, true);
		}
		return null;
	}
}
