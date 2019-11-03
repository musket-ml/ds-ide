package com.onpositive.musket.data.text;

import java.util.ArrayList;
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
}