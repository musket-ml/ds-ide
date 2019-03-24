package com.onpositive.dside.ui.editors.yaml.model;

import java.util.Collection;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.Binding;

public class MetricsRealm implements IRealmProvider<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IRealm<String> getRealm(IHasMeta model, Object parentObject, Object object) {
		Realm<String> realm = new Realm<String>();
		Binding b=(Binding)model;
		b.getParent().getBinding("metrics").addValueListener(new IValueListener<Object>() {

			@Override
			public void valueChanged(Object oldValue, Object newValue) {
				init(realm, b, newValue);
			}
		
		});
		init(realm,b,b.getParent().getBinding("metrics").getValue());
		return realm;
	}
	private void init(Realm<String> realm, Binding b, Object newValue) {
		Collection<Object> collection = ValueUtils.toCollection(newValue);
		for (Object o:collection) {
			if (!realm.contains(o)) {
				realm.remove(o.toString());
			}
		}		
		for (Object o:collection) {
			realm.add(o.toString().toLowerCase());
		}
		for (Object o:collection) {
			realm.add("val_"+o.toString().toLowerCase());
		}
		realm.add("loss");
		realm.add("val_loss");
	}

}
