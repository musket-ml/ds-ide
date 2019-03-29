package com.onpositive.dside.dto;

import java.util.ArrayList;

import com.onpositive.musket_core.ExperimentError;

public class DataSetValidationResult {

	
	public static class ValidationProblem{
		
		protected String message;
		
		protected int item;
		protected Object itemId;
		protected ExperimentError extra;
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public int getItem() {
			return item;
		}
		public void setItem(int item) {
			this.item = item;
		}
		public Object getItemId() {
			return itemId;
		}
		public void setItemId(Object itemId) {
			this.itemId = itemId;
		}
		public ExperimentError getExtra() {
			return extra;
		}
		public void setExtra(ExperimentError extra) {
			this.extra = extra;
		}
	}
	
	public static class DataSetValidation{
		
		
		protected String name="";
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		protected ArrayList<ValidationProblem>problem=new ArrayList<>();
		public ArrayList<ValidationProblem> getProblem() {
			return problem;
		}
		public void setProblem(ArrayList<ValidationProblem> problem) {
			this.problem = problem;
		}
				
	}
	
	protected ArrayList<DataSetValidationResult>results=new ArrayList<>();

	public ArrayList<DataSetValidationResult> getResults() {
		return results;
	}

	public void setResults(ArrayList<DataSetValidationResult> results) {
		this.results = results;
	}
}
