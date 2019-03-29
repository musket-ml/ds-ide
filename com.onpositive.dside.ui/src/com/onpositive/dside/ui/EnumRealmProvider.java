package com.onpositive.dside.ui;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;

@SuppressWarnings({ "rawtypes", "serial" })
public class EnumRealmProvider implements IRealmProvider<Enum>{

	@Override
	public IRealm<Enum> getRealm(IHasMeta model, Object parentObject, Object object) {
		
		Class singleValue = model.getMeta().getSingleValue(DefaultMetaKeys.SUBJECT_CLASS_KEY, Class.class, model);
		return new Realm(singleValue.getEnumConstants());
	}

}
