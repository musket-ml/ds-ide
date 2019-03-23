package com.onpositive.dside.ui.editors.yaml.model;

import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;


public class Registry {

	protected HashMap<String,NodeType>types=new HashMap<>();

	public HashMap<String, NodeType> getTypes() {
		return types;
	}

	public void setTypes(HashMap<String, NodeType> types) {
		this.types = types;
	}

	public NodeType getType(String type) {
		if (types.containsKey(type)) {
			return types.get(type);
		}
		NodeType loadAs = new Yaml().loadAs(Registry.class.getResourceAsStream("/dlf/"+type+".yaml"), NodeType.class);
		types.put(type, loadAs);
		return loadAs;
	}
	public static final Registry INSTANCE=new Registry();
}
