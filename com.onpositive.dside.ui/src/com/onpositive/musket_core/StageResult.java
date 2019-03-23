package com.onpositive.musket_core;

import java.util.ArrayList;
import java.util.Collection;

public class StageResult extends Result{

	protected String name;
	private Object data;
	protected boolean allAttempts;
	protected boolean allStages;
	
	public boolean isAllStages() {
		return allStages;
	}

	public void setAllStages(boolean allStages) {
		this.allStages = allStages;
	}

	public boolean isAllAttempts() {
		return allAttempts;
	}

	public void setAllAttempts(boolean allAttempts) {
		this.allAttempts = allAttempts;
	}

	public StageResult(String name,Object data) {
		this.name=name;
		this.data=data;
	}

	public Collection<? extends String> metrics() {
		if (this.data instanceof java.util.Map) {
			return ((java.util.Map) this.data).keySet();
		}
		return new ArrayList<>();
	}

	@Override
	public Object getMetric(String name) {
		if (this.data instanceof java.util.Map) {
			return ((java.util.Map) this.data).get(name);
		}
		return null;
	}
}
