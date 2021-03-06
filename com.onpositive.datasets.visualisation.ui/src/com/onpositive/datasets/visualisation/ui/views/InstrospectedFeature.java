package com.onpositive.datasets.visualisation.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class InstrospectedFeature {

	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getSourcefile() {
		return sourcefile;
	}

	public void setSourcefile(String sourcefile) {
		this.sourcefile = sourcefile;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public ArrayList<IntrospectedParameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<IntrospectedParameter> parameters) {
		this.parameters = parameters;
	}
	
	Supplier<Collection<String>>values;

	public Supplier<Collection<String>> getValues() {
		return values;
	}

	public void setValues(Supplier<Collection<String>> values) {
		this.values = values;
	}

	String kind;
	
	String sourcefile;
	String source;
	String viewer;
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	String doc;
	boolean custom;
	
	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	ArrayList<IntrospectedParameter>parameters=new ArrayList<>();
	
	public IntrospectedParameter getParameter(String s) {
		for (IntrospectedParameter p:parameters) {
			if (p.getName().equals(s)) {
				return p;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public String getViewer() {
		return viewer;
	}

	public void setViewer(String viewer) {
		this.viewer = viewer;
	}
}
