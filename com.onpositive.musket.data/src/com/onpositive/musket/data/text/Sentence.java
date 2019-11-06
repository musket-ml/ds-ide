package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class Sentence {

	protected ArrayList<Token>tokens=new ArrayList<>();
	protected Document document;

	public Sentence(Document parent) {
		this.document=parent;
	}
	
	public boolean isEmpty() {
		return tokens.isEmpty();
	}

	public void add(Token token) {
		this.tokens.add(token);
	}
	
	@Override
	public String toString() {
		return this.tokens.stream().map(x->x.toString()).collect(Collectors.joining(" "));
	}
	
	public ArrayList<LinkedHashSet<String>>classes(){
		ArrayList<LinkedHashSet<String>>classes=new ArrayList<>();
		this.tokens.forEach(v->{
			ArrayList<String> tclasses = v.classes();
			for (int i=0;i<tclasses.size();i++) {
				String string = tclasses.get(i);
				if (classes.size()<=i) {
					classes.add(new LinkedHashSet<>());
				}
				classes.get(i).add(string.trim());
			}
		});
		return classes;
	}
}