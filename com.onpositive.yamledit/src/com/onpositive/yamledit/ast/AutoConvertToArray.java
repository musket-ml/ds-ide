package com.onpositive.yamledit.ast;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.BuiltIns;
import org.aml.typesystem.ITypeRegistry;
import org.aml.typesystem.Status;
import org.aml.typesystem.meta.facets.Facet;

public class AutoConvertToArray extends Facet<Boolean>{

	public AutoConvertToArray(Boolean value) {
		super(value);
	}

	@Override
	public String facetName() {
		return "autoConvert";
	}

	@Override
	public AbstractType requiredType() {
		return BuiltIns.ANY;
	}

	@Override
	public Status validate(ITypeRegistry registry) {
		return Status.OK_STATUS;
	}

}
