package com.onpositive.dside.ui.editors;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.onpositive.dside.ast.CompletionContext;
import com.onpositive.dside.ast.CompletionContext.IndentAndName;

public class CompletionContextBuilder {

	
	public static CompletionContext build(IDocument document, int offset) {
		try {
			CompletionContext ct=new CompletionContext(offset);
			ct.content=document.get();
			ArrayList<IndentAndName> indents = new ArrayList<>();
			int lineOfOffset = document.getLineOfOffset(offset);
			boolean hasColon=false;
			for (int i = 0; i <= lineOfOffset; i++) {
				IRegion lineInformation = document.getLineInformation(i);
				String string = document.get(lineInformation.getOffset(), lineInformation.getLength());
				IndentAndName indent = ct.getIndent(string);
				indents.add(indent);
				if (i==lineOfOffset) {
					int j = offset-lineInformation.getOffset();
					indent.indent=Math.min(j, indent.indent);
					String substring = string.substring(0, j);
					if (substring.indexOf(':')!=-1) {
						ct.afterKey=true;
					}
					if (string.indexOf(':')!=-1) {
						hasColon=true;
					}
					StringBuilder bld=new StringBuilder();
					for (int a=j-1;a>=0;a--) {
						char charAt = substring.charAt(a);
						if (Character.isJavaIdentifierPart(charAt)) {
							bld.append(charAt);
						}
						else {
							break;
						}
					}
					bld.reverse();
					
					ct.completionStart=bld.toString();
				}
			}
			
			Collections.reverse(indents);
			ArrayList<String> str = new ArrayList<>();
			int minIndent = -1;
			for (IndentAndName i : indents) {
				if (minIndent == -1) {
					minIndent = i.indent;
				}
				if (i.indent == 0) {
					str.add(i.name);
					break;
				}
				if (i.indent < minIndent) {
					str.add(i.name);
					minIndent = i.indent;
				}
			}
			Collections.reverse(str);
			ct.seq=str;
			if (!ct.afterKey&&!hasColon) {
				String substring = ct.content.substring(0, offset);
				int end=Math.max(substring.lastIndexOf('\r'),substring.lastIndexOf('\n'));
				substring=substring.substring(0,end);
				substring=substring+ct.content.substring(offset);
				ct.content=substring;
			}
			return ct;

		} catch (BadLocationException e) {
			return new CompletionContext(offset);
		}
	}
}
