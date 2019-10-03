package com.onpositive.yamledit.model;

import org.aml.typesystem.Status;

import com.onpositive.yamledit.ast.ASTElement;

public interface IValidator {

	Status validate(ASTElement element);
}
