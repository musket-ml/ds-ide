package com.onpositive.musket.data.text;

public class Token {

	private String[] vals;
	private Sentence parent;
	
	
	public Token(Sentence parent,String[] split) {
		this.vals=split;
		this.parent=parent;
	}

	public String[] getVals() {
		return vals;
	}
	
	@Override
	public String toString() {
		return vals[this.parent.document.parent.textPosition()];
	}
}
