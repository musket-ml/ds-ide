package com.onpositive.dside.ui.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

public class FragmentExtractor {

	public static String extractFragment(IDocument viewer) {
		try {
			IRegion lineInformation =viewer.getLineInformation(0);
			String string = viewer.get(lineInformation.getOffset(), lineInformation.getLength());
			if (!string.startsWith("#%")) {
				return null;
			}
			int indexOf = string.indexOf(" ");
			if (indexOf>0) {
				String string2 = string.substring(indexOf).trim();
				int indexOf2 = string2.indexOf(' ');
				if (indexOf2>0) {
					string2=string2.substring(0,indexOf2);
					return string2;
				}
				return string2;
			}
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
