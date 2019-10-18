package com.onpositive.dside.dto;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.ui.generic.annotations.ContentAssist;

@Display("dlf/filter.dlf")
public class DataSetFilter {

	protected String filterKind;

	
	protected String filterArgs;
	
	protected String applyAt;
	
	protected String mode;
	
	private ArrayList<String>kinds=new ArrayList<>();
	private ArrayList<String>stages=new ArrayList<>();
	private ArrayList<String>modes=new ArrayList<>();

	@Required
	public String getFilterKind() {
		return filterKind;
	}

	public void setFilterKind(String filterKind) {
		this.filterKind = filterKind;
	}

	
	public String getFilterArgs() {
		return filterArgs;
	}

	public void setFilterArgs(String filterArgs) {
		this.filterArgs = filterArgs;
	}

	@Required
	public String getApplyAt() {
		return applyAt;
	}

	public void setApplyAt(String applyAt) {
		this.applyAt = applyAt;
	}


	
	public Collection<String>getStages(){ 
		return stages;
	}
	public Collection<String>getKinds(){ 
		return kinds;
	}
	public Collection<String>getModes(){ 
		return modes;
	}

	
	@Required
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
