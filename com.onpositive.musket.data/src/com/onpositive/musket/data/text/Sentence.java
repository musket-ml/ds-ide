package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class Sentence {

	protected ArrayList<Token>tokens=new ArrayList<>();
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}

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

	public ArrayList<Token> tokens() {
		return tokens;
	}
}