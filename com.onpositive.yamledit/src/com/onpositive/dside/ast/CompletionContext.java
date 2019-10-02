package com.onpositive.dside.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class CompletionContext {

	public ArrayList<String> seq=new ArrayList<>();
	public boolean afterKey=false;
	public String completionStart;
	public String content;
	public int completionOffset;
	
	protected static HashMap<String, String>contexts=new HashMap<>();
	
	
	static {
		contexts.put("datasets", "dataset_factory");
		contexts.put("callbacks", "Callback");
		contexts.put("loss", "losses");
		contexts.put("metrics", "metrics");
		contexts.put("declarations", "preprocessor,Layer,model");
	}
	
	public CompletionContext(int offset) {
		this.completionOffset=offset;
	}

	

	public static class IndentAndName {
		public IndentAndName(String trim, int level) {
			this.name = trim;
			this.indent = level;
		}

		public String name;
		public int indent;
	}

	public IndentAndName getIndent(String string) {
		int level = 0;
		for (int j = 0; j < string.length(); j++) {
			char charAt = string.charAt(j);
			if (Character.isWhitespace(charAt)) {
				if (charAt == ' ') {
					level += 1;
				}
				if (charAt == '\t') {
					level += 4;
				}
			} else {
				if (charAt == '-') {
					level += 1;
					continue;
				}
				int ls = string.indexOf(':');
				if (ls != -1) {
					String trim = string.substring(j, ls).trim();
					return new IndentAndName(trim, level);
				}
				else {
					return new IndentAndName("", level);
				}
			}
		}
		return new IndentAndName("", level);
	}

	public ArrayList<String> getSeq() {
		return seq;
	}

	public static HashMap<String, String> getContexts() {
		return contexts;
	}

	public void setSeq(ArrayList<String> seq) {
		this.seq = seq;
	}

}
