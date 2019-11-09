package com.onpositive.musket.data.core;

import java.util.LinkedHashMap;

public class DataSetMemento {

	public String url;

	public LinkedHashMap<String, Object>settings=new LinkedHashMap<String, Object>();

	public DataSetMemento(String url) {
		super();
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}

	public LinkedHashMap<String, Object> getSettings() {
		return settings;
	}
	
	public void setSettings(LinkedHashMap<String, Object> settings) {
		this.settings = settings;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	protected String encoding="UTF-8";
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public final static String ORIGIN="origin";

	public static final String TARGET = "location";
	
	public static final String KIND = "kind";
	
	public DataSetMemento getOriginal() {
		DataSetMemento dataSetMemento = new DataSetMemento((String) this.settings.get(ORIGIN));
		if (dataSetMemento.url==null) {
			return null;
		}
		dataSetMemento.settings.putAll(this.settings);
		dataSetMemento.settings.remove(ORIGIN);
		return dataSetMemento;
	}

	public String getKind() {
		return (String) this.settings.get(KIND);
	}

	public String getTarget() {
		return (String) this.settings.get(TARGET);
	}

	public DataSetMemento withTarget() {
		DataSetMemento dataSetMemento = new DataSetMemento((String) this.settings.get(TARGET));
		if (dataSetMemento.url==null) {
			return null;
		}
		dataSetMemento.settings.putAll(this.settings);
		dataSetMemento.settings.remove(TARGET);
		dataSetMemento.settings.put(ORIGIN,this.url);
		return dataSetMemento;
		
	}
}
