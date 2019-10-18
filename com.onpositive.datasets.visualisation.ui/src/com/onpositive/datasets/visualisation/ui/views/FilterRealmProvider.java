package com.onpositive.datasets.visualisation.ui.views;

import java.util.Collection;
import java.util.Collections;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;

public class FilterRealmProvider implements IRealmProvider<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IRealm<String> getRealm(IHasMeta arg0, Object arg1, Object arg2) {
		DataSetFilter f1=(DataSetFilter) arg1;
		Realm<String> realm = new Realm<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Collection<String> getContents() {
				for (InstrospectedFeature df:f1.features) {
					if (df.getName().equals(f1.getFilterKind())) {
						if (df.values!=null) {
							return df.values.get();
						}
					}
				}
				return Collections.emptyList();
			}
		};
		realm.add("Hello");
		realm.add("World");
		return realm;
	}

}
