package com.onpositive.yamledit.ast;

import java.util.HashSet;

import org.aml.typesystem.Status;
import org.aml.typesystem.values.IArray;

import com.onpositive.yamledit.model.IValidator;

public class CallbackValidator implements IValidator{

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
		Object property2 = element.getProperty("monitor");
		if (property2!=null) {
			if (!metrics.contains(property2.toString())) {
				Status status = new Status(Status.ERROR,1,"monitor should be one of:"+metrics);
				status.setKey("monitor");
				res.addSubStatus(status);
			}
		}
		return res;
	}

}
