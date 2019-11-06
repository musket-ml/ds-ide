package com.onpositive.musket.data.text;

import java.util.ArrayList;

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

	public ArrayList<String> classes() {
		ArrayList<String>rs=new ArrayList<>();
		for (int i=this.parent.document.parent.textPosition()+1;i<vals.length;i++) {
			rs.add(this.vals[i]);
		}
		return rs;		
	}

	
	
	
}
