package com.onpositive.yamledit.introspection;

import java.util.ArrayList;

public class InstrospectedFeature {

	boolean custom;

	String doc;

	String kind;

	String name;

	ArrayList<IntrospectedParameter>parameters=new ArrayList<>();

	String source;

	String sourcefile;

	String viewer;

	public String getDoc() {
		return doc;
	}

	public String getKind() {
		return kind;
	}

	public String getName() {
		return name;
	}

	public IntrospectedParameter getParameter(String s) {
		for (IntrospectedParameter p:parameters) {
			if (p.getName().equals(s)) {
				return p;
			}
		}
		return null;
	}
	
	public ArrayList<IntrospectedParameter> getParameters() {
		return parameters;
	}
	public String getSource() {
		return source;
	}
	public String getSourcefile() {
		return sourcefile;
	}
	public String getViewer() {
		return viewer;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParameters(ArrayList<IntrospectedParameter> parameters) {
		this.parameters = parameters;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setSourcefile(String sourcefile) {
		this.sourcefile = sourcefile;
	}

	public void setViewer(String viewer) {
		this.viewer = viewer;
	}

	@Override
	public String toString() {
		return this.kind != null ? this.name + ":" + this.kind : this.name;
	}
	
	
}
