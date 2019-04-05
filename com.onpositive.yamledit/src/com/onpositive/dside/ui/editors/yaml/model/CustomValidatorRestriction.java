package com.onpositive.dside.ui.editors.yaml.model;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.BuiltIns;
import org.aml.typesystem.ITypeRegistry;
import org.aml.typesystem.Status;
import org.aml.typesystem.meta.restrictions.AbstractRestricton;

import com.onpositive.dside.ast.ASTElement;

public class CustomValidatorRestriction extends AbstractRestricton{

	protected String value;
	private IValidator validator;
	
	public CustomValidatorRestriction(String value) {
		super();
		this.value = value;
		try {
			this.validator=(IValidator)Class.forName(value).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public Status check(Object o) {
		if (o instanceof ASTElement) {
			ASTElement el=(ASTElement) o;
			return this.validator.validate(el);
		}
		return Status.OK_STATUS;
	}

	@Override
	protected AbstractRestricton composeWith(AbstractRestricton restriction) {
		return null;
	}

	@Override
	public String facetName() {
		return "custom";
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
