package com.onpositive.musket.data.core;

import java.util.Collection;
import java.util.function.Supplier;

public interface IProto {


	String name();
	
	Parameter[] parameters();

	String id();
	
	Supplier<Collection<String>>values();
}
