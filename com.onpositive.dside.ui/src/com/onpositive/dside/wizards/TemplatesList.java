package com.onpositive.dside.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.yaml.snakeyaml.Yaml;

public class TemplatesList {


	ArrayList<Template>templates=new ArrayList<>();

	public ArrayList<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(ArrayList<Template> templates) {
		this.templates = templates;
	}
	
	
	public static TemplatesList getTemplatesList() {
		Yaml y = new Yaml();
		InputStream resourceAsStream = TemplatesList.class.getResourceAsStream("/templates/templates.yaml");
		try {
		TemplatesList loadAs = y.loadAs(resourceAsStream, TemplatesList.class);
		return loadAs;
		}finally {
			try {
				resourceAsStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
