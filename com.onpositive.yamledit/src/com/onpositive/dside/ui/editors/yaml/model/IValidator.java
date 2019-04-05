package com.onpositive.dside.ui.editors.yaml.model;

import org.aml.typesystem.Status;

import com.onpositive.dside.ast.ASTElement;

public interface IValidator {

	Status validate(ASTElement element);
}
