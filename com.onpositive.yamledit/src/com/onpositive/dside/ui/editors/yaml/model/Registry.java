package com.onpositive.dside.ui.editors.yaml.model;

import java.io.InputStream;
import java.io.InputStreamReader;
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
		InputStream resourceAsStream = Registry.class.getResourceAsStream("/schema/"+type+".yaml");
		NodeType loadAs = new Yaml().loadAs(new InputStreamReader(resourceAsStream), NodeType.class);
		types.put(type, loadAs);
		return loadAs;
	}
	public static final Registry INSTANCE=new Registry();
}
