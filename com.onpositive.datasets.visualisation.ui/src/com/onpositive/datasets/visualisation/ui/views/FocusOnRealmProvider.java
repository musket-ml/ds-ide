package com.onpositive.datasets.visualisation.ui.views;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;

public class FocusOnRealmProvider implements IRealmProvider<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IRealm<String> getRealm(IHasMeta arg0, Object arg1, Object arg2) {
		Realm<String> realm = new Realm<>();
		FocusOnModel mod=(FocusOnModel) arg1;
		realm.add(mod.classes);
		return realm;
	}

}
