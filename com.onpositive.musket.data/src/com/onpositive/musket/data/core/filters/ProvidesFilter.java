package com.onpositive.musket.data.core.filters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProvidesFilter {

	String value();
}
