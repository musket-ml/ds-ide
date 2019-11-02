package com.onpositive.datasets.visualisation.ui.views;

import java.io.File;
import java.util.ArrayList;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;

public class BertRealmProvider implements IRealmProvider<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public IRealm<String> getRealm(IHasMeta arg0, Object arg1, Object arg2) {
		TextClassificationTemplate ts=(TextClassificationTemplate) arg1;
		String p=ts.projectPath;
		
		@SuppressWarnings("rawtypes")
		Realm realm = new Realm();
		File file = new File(p,"data");
		for (File f:file.listFiles()) {
			visit(f, file.getAbsolutePath(), realm);
		}		
		return realm;
	}
	
	protected void visit(File fl,String p,Realm<String>paths) {
		if (fl.isDirectory()) {
			if (new File(fl,"bert_config.json").exists()) {
				paths.add(fl.getAbsolutePath().substring(p.length()).replace('\\', '/'));
			}
			for (File a:fl.listFiles()) {
				if (a.isDirectory()) {
					visit(a, p, paths);
				}
			}
		}
	}

}
