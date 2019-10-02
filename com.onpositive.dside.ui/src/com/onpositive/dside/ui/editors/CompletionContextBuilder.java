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
			CompletionContext context=new CompletionContext(offset);
			context.content=document.get();
			ArrayList<IndentAndName> indents = new ArrayList<>();
			int lineOfOffset = document.getLineOfOffset(offset);
			boolean hasColon=false;
			for (int i = 0; i <= lineOfOffset; i++) {
				IRegion lineInformation = document.getLineInformation(i);
				String line = document.get(lineInformation.getOffset(), lineInformation.getLength());
				IndentAndName indent = context.getIndent(line);
				indents.add(indent);
				if (i==lineOfOffset) {
					int preffixLen = offset-lineInformation.getOffset();
					indent.indent=Math.min(preffixLen, indent.indent);
					String preffix = line.substring(0, preffixLen);
					int idx = line.indexOf(':');
					if (idx >=  0) {
						hasColon=true;
						if (idx < preffixLen) {
							context.afterKey=true;
						}
					}
					StringBuilder bld=new StringBuilder();
					for (int j=preffixLen-1;j>=0;j--) {
						char charAt = preffix.charAt(j);
						if (Character.isJavaIdentifierPart(charAt)) {
							bld.append(charAt);
						}
						else {
							break;
						}
					}
					bld.reverse();
					
					context.completionStart=bld.toString();
				}
			}
			
			Collections.reverse(indents);
			ArrayList<String> strings = new ArrayList<>();
			int minIndent = -1;
			for (IndentAndName i : indents) {
				if (minIndent == -1) {
					minIndent = i.indent;
				}
				if (i.indent == 0) {
					strings.add(i.name);
					break;
				}
				if (i.indent < minIndent) {
					strings.add(i.name);
					minIndent = i.indent;
				}
			}
			Collections.reverse(strings);
			context.setSeq(strings);
			if (!context.afterKey && !hasColon) {
				String substring = context.content.substring(0, offset);
				int end = Math.max(substring.lastIndexOf('\r'), substring.lastIndexOf('\n'));
				String sm = substring.substring(end);
				if (!sm.trim().isEmpty()) {
					if (Character.isJavaIdentifierPart(substring.charAt(substring.length() - 1))) {
						substring = substring + ":";
					}
				} else {
					substring = substring.substring(0, end);
				}
				substring = substring + context.content.substring(offset);
				context.content = substring;
			}
			return context;

		} catch (BadLocationException e) {
			return new CompletionContext(offset);
		}
	}
}
