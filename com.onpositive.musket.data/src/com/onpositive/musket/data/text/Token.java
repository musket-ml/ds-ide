package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.Arrays;

public class Token {

	private String[] vals;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(vals);
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
		Token other = (Token) obj;
		if (!Arrays.equals(vals, other.vals))
			return false;
		return true;
	}

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
