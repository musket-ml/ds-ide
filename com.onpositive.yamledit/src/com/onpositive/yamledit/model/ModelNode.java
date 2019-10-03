package com.onpositive.yamledit.model;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.Yaml;

import com.onpositive.semantic.model.api.property.IHasPropertyProvider;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Image;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.semantic.model.ui.generic.IKnowsImageObject;

@TextLabel(provider = ModelNodeLabelProvider.class)
public class ModelNode implements IHasPropertyProvider,IKnowsImageObject {

	protected Object object;
	protected NodeKind clazz;
	protected String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}
	public boolean hasChildren() {
		return this.clazz.hasChildren();
	}

	public ArrayList<ModelNode> getChildren() {
		if (!this.clazz.hasChildren()) {
			return new ArrayList<>();
		}
		ArrayList<ModelNode> res = new ArrayList<>();
		if (this.object instanceof Map) {
			Map<String, Object> m = (Map) this.object;
			m.entrySet().forEach(f -> {
				Object value = f.getValue();
				if (value instanceof Boolean || value instanceof String || value instanceof Number) {
					return;
				} else {
					ModelNode ma = new ModelNode(value, clazz.createChild(value));
					ma.key = f.getKey();
					res.add(ma);
				}
			});
		}
		if (this.object instanceof ArrayList) {
			for (Object o : ((ArrayList) this.object)) {
				Object value = o;
				if (value instanceof Boolean || value instanceof String || value instanceof Number) {
					ModelNode ma = new ModelNode(value, clazz.createChild(value));
					res.add(ma);
					ma.setKey(value.toString());
				} else {
					if (value instanceof Map) {
						Map m = (Map) value;
						if (m.size() == 1) {
							Object next = m.keySet().iterator().next();
							ModelNode ma = new ModelNode(m.get(next), clazz.createChild(m.get(next)));
							ma.setKey(next.toString());
							res.add(ma);
						}
					} else {
						ModelNode ma = new ModelNode(value, clazz.createChild(value));
						res.add(ma);
					}
				}
			}
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj.equals(this.object)) {
			return true;
		}
		if (getClass() != obj.getClass())
			return false;
		ModelNode other = (ModelNode) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}

	public ModelNode(Object object, NodeKind knd) {
		super();
		this.clazz = knd;
		this.object = object;
	}

	public ModelNode(String str, NodeKind knd) {
		Object loadAs = new Yaml().loadAs(new StringReader(str), Object.class);
		this.object = loadAs;
		this.clazz = knd;
	}

	public String toYaml() {
		StringWriter stringWriter = new StringWriter();
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
		dumperOptions.setPrettyFlow(true);
		Yaml yaml = new Yaml(new Representer() {
			@Override
			protected Node representSequence(Tag arg0, Iterable<? extends Object> arg1, Boolean arg2) {
				boolean hasNotScalars = false;
				for (Object o : arg1) {
					if (o instanceof String || o instanceof Number || o instanceof Boolean) {
						continue;
					}
					hasNotScalars = true;
				}
				if (hasNotScalars) {
					setDefaultFlowStyle(FlowStyle.BLOCK);
				} else {
					setDefaultFlowStyle(FlowStyle.FLOW);
				}
				Node representSequence = super.representSequence(arg0, arg1, arg2);
				setDefaultFlowStyle(FlowStyle.BLOCK);
				return representSequence;
			}
		});

		yaml.dump(object, stringWriter);
		return stringWriter.toString();
	}

	@Override
	public String toString() {
		if (this.key!=null&&this.key.length()>0) {
			return this.key+": "+this.toYaml();
		}
		return this.toYaml();
	}

	@Override
	public IPropertyProvider getPropertyProvider() {
		return this.clazz;
	}

	public void update(String string) {
		Object loadAs = new Yaml().loadAs(new StringReader(string), Object.class);
		this.object = loadAs;
	}

	@Override
	public String getImageID() {
		return this.clazz.nodetype.getIcon();
	}

}
