package com.onpositive.datasets.visualisation.ui.views;

import java.io.File;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;

public class EmbeddingsRealmProvider implements IRealmProvider<String>{

	@Override
	public IRealm<String> getRealm(IHasMeta arg0, Object arg1, Object arg2) {
		TextClassificationTemplate ts=(TextClassificationTemplate) arg1;
		String p=ts.projectPath;
		
		Realm realm = new Realm();
		for (File f:new File(p,"data").listFiles()) {
			if (f.getName().endsWith(".txt")) {
				realm.add(f.getName());
			}
		}		
		return realm;
	}

}
