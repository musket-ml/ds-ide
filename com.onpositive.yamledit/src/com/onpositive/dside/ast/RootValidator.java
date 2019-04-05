package com.onpositive.dside.ast;

import java.util.HashSet;

import org.aml.typesystem.Status;
import org.aml.typesystem.values.IArray;

import com.onpositive.dside.ui.editors.yaml.model.IValidator;

public class RootValidator implements IValidator{

	@Override
	public Status validate(ASTElement element) {
		Object property = element.getRoot().getProperty("metrics");
		Status res=new Status(0, 0, "");
		HashSet<String>metrics=new HashSet<>();
		if (property instanceof IArray) {
			IArray arr=(IArray) property;
			for (int i=0;i<arr.length();i++) {
				metrics.add(arr.item(i).toString());
				metrics.add("val_"+arr.item(i).toString());
			}			
		}
		metrics.add("loss");
		metrics.add("val_loss");
		Object property2 = element.getProperty("primary_metric");
		if (property2!=null) {
			if (!metrics.contains(property2.toString())) {
				Status status = new Status(Status.ERROR,1,"primary metric should be one of:"+metrics);
				status.setKey("primary_metric");
				res.addSubStatus(status);
			}
		}
		//res.setSeverity(0);
		return res;
	}

}
