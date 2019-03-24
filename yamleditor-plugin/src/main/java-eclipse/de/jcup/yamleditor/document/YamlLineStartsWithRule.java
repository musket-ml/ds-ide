/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.yamleditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public abstract class YamlLineStartsWithRule implements IPredicateRule {

	private IToken successToken;
	private char[] startsWith;
	private char[] endsWith;
	private boolean multiLines;
	private boolean successAfterStartFound;

	public YamlLineStartsWithRule(String startsWith, String endsWith, boolean successAfterStartFound, boolean multiLines, IToken token) {
		this.successToken = token;
		this.multiLines = multiLines;
		this.startsWith = startsWith.toCharArray();
		this.endsWith = endsWith == null ? new char[0] : endsWith.toCharArray();
		this.successAfterStartFound=successAfterStartFound;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken getSuccessToken() {
		return successToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		int count = 0;
		int pos=0;
		while (pos< startsWith.length) {
			int c = scanner.read();
			count++;
			if (c == ICharacterScanner.EOF || c == '\n' || c == '\r') {
				return resetScannerAndReturnUndefined(scanner, count);
			}
			if ((startsWith[pos] != c && isAcceptedAtStart(c))){
				pos=0; // reset pos
				continue;
			}
			if (startsWith[pos++] != c) {
				return resetScannerAndReturnUndefined(scanner, count);
			}
		}
		/* found start word */
		if (successAfterStartFound){
			return getSuccessToken();
		}
		boolean noEndsWithScanNecessary = endsWith.length == 0;
		int c = -1;
		int endsWithPos = 0;
		while (true) {
			c = scanner.read();
			count++;
			if (c == ICharacterScanner.EOF) {
				/* end of file */
				if (noEndsWithScanNecessary) {
					return getSuccessToken();
				}
				/* token not found currently */
				return resetScannerAndReturnUndefined(scanner, count);
			}
			if (c == '\n') {
				/* new line */
				if (multiLines) {
					/* keep on going but start from fresh */
					endsWithPos = 0;
					continue;
				} else {
					if (noEndsWithScanNecessary) {
						return getSuccessToken();
					}
					/* only one line, so token not found currently */
					return resetScannerAndReturnUndefined(scanner, count);
				}
			}
			if (noEndsWithScanNecessary) {
				continue;
			}
			/* ENDSWITH is set */
			
			
			if (multiLines){
				if (endsWithPos < endsWith.length) {
					if (c == endsWith[endsWithPos]) {
						endsWithPos++;
					}else{
						// car not does not match on position so this line fails
						// we set the position 1 behind last one so it goes to continue
						// until next new line 
						endsWithPos= endsWith.length+1;
						continue;
					}
					if (endsWithPos == endsWith.length) {
						/* found exact so success */
						return getSuccessToken();
					}
				} else {
					// just continue ...
					continue;
				}
			}else{
				/* single line - just scan until end of line */
				if (endsWithPos < endsWith.length) {
					if (c == endsWith[endsWithPos]) {
						endsWithPos++;
					}
					if (endsWithPos == endsWith.length) {
						/* found so success */
						return getSuccessToken();
					}
				} else {
					return resetScannerAndReturnUndefined(scanner, count);
				}
			}
		}
	}

	protected abstract boolean isAcceptedAtStart(int c);

	private IToken resetScannerAndReturnUndefined(ICharacterScanner scanner, int count) {
		while (count > 0) {
			scanner.unread();
			count--;
		}
		return Token.UNDEFINED;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(":starsWith='");
		sb.append(startsWith);
		sb.append("', endsWith='");
		sb.append(endsWith);
		sb.append("', multiLines=");
		sb.append(multiLines);
		return sb.toString();
	}

}
