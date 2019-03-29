package com.onpositive.dside.ui.editors.yaml.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.AbstractWritableProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;

final class MapProperty extends AbstractWritableProperty implements IProperty {

	/**
	 * 
	 */
	private final NodeKind nodeKind;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String arg1;

	MapProperty(NodeKind nodeKind, String arg1) {
		super(arg1);
		this.nodeKind = nodeKind;
		this.arg1 = arg1;
	}

	@Override
	public IMeta getMeta() {
		BaseMeta meta = (BaseMeta) super.getMeta();
		PropertyDescription propertyDescription = this.nodeKind.nodetype.getProperties().get(arg1);
		if (propertyDescription != null && propertyDescription.multivalue) {
			this.typeIsCollection = true;
			this.type = ArrayList.class;
			meta.putMeta(DefaultMetaKeys.MULTI_VALUE_KEY, true);
		}

		if (propertyDescription != null) {
			if (propertyDescription.step != null) {
				meta.putMeta(DefaultMetaKeys.RANGE_DIGITS__KEY, 2);
				meta.putMeta(DefaultMetaKeys.RANGE_INCREMENT__KEY, propertyDescription.step);
				if (propertyDescription.max != null) {
					meta.putMeta(DefaultMetaKeys.RANGE_MAX__KEY, propertyDescription.max);
				}
				if (propertyDescription.min != null) {
					meta.putMeta(DefaultMetaKeys.RANGE_MIN__KEY, propertyDescription.min);
				}
			}
			if (propertyDescription.fixedValues != null) {
				meta.putMeta(DefaultMetaKeys.FIXED_BOUND_KEY, true);
				Realm<Object> realm = new Realm<>(Arrays.asList((Object[]) propertyDescription.fixedValues));
				meta.putMeta(DefaultMetaKeys.REALM__KEY, realm);
				meta.registerService(IRealmProvider.class, new IRealmProvider<Object>() {

					@Override
					public IRealm<Object> getRealm(IHasMeta model, Object parentObject, Object object) {

						return realm;
					}
				});

			}
			if (propertyDescription.customRealm != null) {
				try {
					meta.registerService(IRealmProvider.class,
							(IRealmProvider) Class.forName(propertyDescription.customRealm).newInstance());
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return meta;
	}

	@Override
	public Object getValue(Object arg0) {
		ModelNode m = (ModelNode) arg0;
		if (arg1.equals("Children")) {
			return m.getChildren();
		}
		PropertyDescription propertyDescription = this.nodeKind.nodetype.getProperties().get(arg1);
		boolean isObject = isObject(propertyDescription);
		Object object = ((Map) m.object).get(arg1);
		if (object == null) {
			if (propertyDescription != null) {
				if (propertyDescription.defaultValue != null) {
					return propertyDescription.getDefaultValue();
				}
			}
		}

		if (propertyDescription != null) {

			if (propertyDescription.isLowerCase()) {
				if (object != null) {
					return object.toString().toLowerCase();
				}
			}
		}
		if (isObject) {

			if (object instanceof Map) {
				Map<String, Object> ma = (Map) object;
				ArrayList<ModelNode> nodes = new ArrayList<>();
				for (String s : ma.keySet()) {
					ModelNode n = new ModelNode(ma.get(s), getKind(s));
					nodes.add(n);
					n.setKey(s);
				}
				return nodes;
			} else if (object instanceof ArrayList) {

				ArrayList<ModelNode> nodes = new ArrayList<>();
				for (Object s : (ArrayList) object) {
					ModelNode n = new ModelNode(s, getKind(null));
					nodes.add(n);
				}
				return nodes;
			} else {
				ModelNode mn = new ModelNode(object, getKind(null));
				return mn;
			}
		}
		return object;
	}

	private NodeKind getKind(String key) {
		
		PropertyDescription propertyDescription = this.nodeKind.nodetype.getProperties().get(arg1);
		if (propertyDescription!=null) {
			String range = propertyDescription.getRange();
			if (range!=null) {
				NodeKind nodeKind2 = new NodeKind(range, this.nodeKind.listener);
				
				return nodeKind2;
			}
		}
		return this.nodeKind;
	}

	private boolean isObject(PropertyDescription propertyDescription) {
		boolean isObject = false;
		String type2 = propertyDescription != null ? propertyDescription.getType() : "";
		if (type2 != null && type2.equals("object")) {
			isObject = true;
		}
		return isObject;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void doSet(Object target, Object object) throws IllegalAccessException {
		ModelNode m = (ModelNode) target;
		PropertyDescription propertyDescription = this.nodeKind.nodetype.getProperties().get(arg1);
		boolean isObject = isObject(propertyDescription);
		Object v = ((Map) m.object).get(arg1);
		if (isObject) {

			if (object instanceof ArrayList) {
				LinkedHashMap<String, Object> ms = new LinkedHashMap<>();
				boolean isMap = true;
				for (Object o : (ArrayList<?>) object) {
					if (o instanceof ModelNode) {
						ModelNode ma = (ModelNode) o;
						if (ma.getKey() != null) {
							ms.put(ma.getKey(), ma.object);
						} else {
							isMap = false;
						}
					} else {
						isMap = false;
					}
				}
				if (isMap) {
					object = ms;
				} else {
					ArrayList<Object>zzz=new ArrayList<>();
					for (Object o : (ArrayList<?>) object) {						
						ModelNode ma = (ModelNode) o;
						zzz.add(ma);						
					}
					object=zzz;
				}
			}
			if (object instanceof ModelNode) {
				ModelNode n = (ModelNode) object;
				LinkedHashMap<String, Object> rs = new LinkedHashMap<>();
				if (n.getKey() != null) {
					rs.put(n.getKey(), n.object);
				}
				if (n.object instanceof String) {
					object=n.object;
				}
				else {
					object = rs;
				}
			}
			if (object instanceof String) {
				try {
				object=new Yaml().load(new StringReader((String) object));
				}catch (Exception e) {
				}
			}
		}
		if (propertyDescription!=null&&propertyDescription.type!=null&&propertyDescription.getType().equals("int")) {
			try {
				if (object instanceof String) {
					object=Integer.parseInt((String) object);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (propertyDescription!=null&&propertyDescription.type!=null&&propertyDescription.getType().equals("float")) {
			try {
				if (object instanceof String) {
					object=Double.parseDouble((String) object);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (v == object || (v != null && v.equals(object))) {
			return;
		}

		Map<String, Object> map = (Map<String, Object>) m.object;
		if (object == null) {
			map.remove(arg1);
		} else
			map.put(arg1, object);
		this.nodeKind.listener.updated(m, object, arg1);
	}
}