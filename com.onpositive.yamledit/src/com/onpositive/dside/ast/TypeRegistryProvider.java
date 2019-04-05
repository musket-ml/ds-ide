package com.onpositive.dside.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.BuiltIns;
import org.aml.typesystem.ITypeRegistry;
import org.aml.typesystem.InheritedType;
import org.aml.typesystem.TypeOps;
import org.aml.typesystem.TypeRegistryImpl;
import org.aml.typesystem.meta.facets.HasKey;
import org.aml.typesystem.meta.facets.IsRef;
import org.aml.typesystem.meta.restrictions.DefaultPropertyMeta;

import com.onpositive.dside.ui.editors.yaml.model.CustomValidatorRestriction;
import com.onpositive.dside.ui.editors.yaml.model.NodeType;
import com.onpositive.dside.ui.editors.yaml.model.PropertyDescription;
import com.onpositive.dside.ui.editors.yaml.model.Registry;

public class TypeRegistryProvider {

	HashMap<String, TypeRegistryImpl>impl=new HashMap<>();
	
	public static Universe getRegistry(String typeName) {		
		Universe res=new Universe(BuiltIns.getBuiltInTypes());
		NodeType type = Registry.INSTANCE.getType(typeName);
		
		Map<String, NodeType> typesToRegister = type.getTypes();
		
		for (String s:typesToRegister.keySet()) {
			res.registerType(TypeOps.derive(s, BuiltIns.OBJECT));
		}
		AbstractType ref=null;
		for (String s:typesToRegister.keySet()) {
			NodeType nodeType = typesToRegister.get(s);
			AbstractType type2 = res.getType(s);
			String lm = nodeType.getType();
			
			if (lm!=null) {
				if (typesToRegister.containsKey(lm)) {
					InheritedType r=(InheritedType) type2;
					
					r.setSuperType(res.getType(lm));
				}
				if (lm.indexOf(',')!=-1) {
					InheritedType r=(InheritedType) type2;
					ArrayList<AbstractType>rt=new ArrayList<>();
					String[] split = lm.split(",");
					for (String sa:split) {
						rt.add(res.getType(sa));
					}
					r.setSuperTypes(rt.toArray(new AbstractType[rt.size()]));
				}
			}
			
			
			if (nodeType.isRoot()) {
				ref=type2;
			}
			String defaultProperty = nodeType.getDefaultProperty();
			if (defaultProperty!=null) {
				type2.addMeta(new DefaultPropertyMeta(defaultProperty));
			}
			fill(type2,nodeType,res);
			type2.closeUnknownProperties();
		}
		res.setRoot(ref);
		return res;	
	}

	private static void fill(AbstractType type2, NodeType nodeType,ITypeRegistry reg) {
		if (nodeType.getCustomValidator()!=null) {
			type2.addMeta(new CustomValidatorRestriction(nodeType.getCustomValidator()));
		}
		for (String s:nodeType.getProperties().keySet()) {
			PropertyDescription propertyDescription = nodeType.getProperties().get(s);
			String type = propertyDescription.getType();
			
			if (type==null) {
				type="string";
			}
			if (type.equals("float")) {
				type="number";
			}
			if (type.equals("int")) {
				type="integer";
			}
			AbstractType type3 = reg.getType(type);
			if (type3==null) {
				if (type.endsWith("[]")) {
					type3=reg.getType(type.substring(0,type.length()-2));
					type3=TypeOps.array(type3);
				}
				if (type3==null) {
				throw new NullPointerException(propertyDescription.getType());
				}
			}
			
			if (propertyDescription.isMultivalue()) {
				type3=TypeOps.array(type3);
				if(type3.componentType().isObject()) {
					type3.addMeta(new HasKey(true));
				}
			}
			
			if (propertyDescription.isAutoConvertToArray()) {
				if (type3.isBuiltIn()) {
					type3=TypeOps.derive(type3.name(), type3);
				}
				type3.addMeta(new AutoConvertToArray(true));
			}
			if (propertyDescription.isReference()) {
				//type3=TypeOps.derive(type3.name(), type3);
				type3.addMeta(new IsRef(true));
			}
			type2.declareProperty(s, type3, !propertyDescription.isRequired());
		}
	}
}
