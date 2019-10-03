/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this editorFile except in compliance with the License.
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
package com.onpositive.dside.ui.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;
import org.python.pydev.editorinput.PyOpenEditor;
import org.python.pydev.shared_core.io.FileUtils;

import com.onpositive.musket_core.ProjectWrapper;
import com.onpositive.yamledit.introspection.InstrospectedFeature;
import com.onpositive.yamledit.introspection.InstrospectionResult;

import de.jcup.yamleditor.script.YamlNode;

/**
 * Hyperlink detector for all kind of hyperlinks in egradle editor.
 * 
 * @author Albert Tregnaghi
 *
 */
public class YamlHyperlinkDetector extends AbstractHyperlinkDetector {

	private final class MusketHyperLink implements IHyperlink {
		private final InstrospectedFeature f;
		private final Region targetRegion;

		private MusketHyperLink(InstrospectedFeature f, Region targetRegion) {
			this.f = f;
			this.targetRegion = targetRegion;
		}

		@Override
		public void open() {
			String sourcefile = f.getSourcefile();
			String absPath = FileUtils.getFileAbsolutePath((File) new File(sourcefile));
			IPath path = Path.fromOSString(absPath);
			ITextEditor editor = (ITextEditor) PyOpenEditor.doOpenEditor(new File(sourcefile));
			IDocument document2 = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			String string = document2.get();
			BufferedReader bufferedReader = new BufferedReader(new StringReader(string));
			int line = 0;
			int actualLine = -1;
			while (true) {
				try {
					String readLine = bufferedReader.readLine();
					if (readLine == null) {
						break;
					}
					readLine = readLine.trim();
					line = line + 1;
					if (readLine.startsWith("class") || readLine.startsWith("def")) {
						int indexOf = readLine.indexOf(' ');
						readLine = readLine.substring(indexOf).trim();
						if (readLine.toLowerCase().startsWith(f.getName().toLowerCase())) {
							String ddd = readLine.substring(f.getName().toLowerCase().length());
							if (ddd.length() > 0) {
								if (!Character.isJavaIdentifierPart(ddd.charAt(0))) {
									actualLine = line;
								}
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (actualLine != -1) {
				int lineOffset;
				try {
					lineOffset = document2.getLineOffset(actualLine - 1);
					editor.selectAndReveal(lineOffset, 0);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		@Override
		public String getTypeLabel() {
			return "Hello";
		}

		@Override
		public String getHyperlinkText() {
			return f.getName() + " " + f.getKind();
		}

		@Override
		public IRegion getHyperlinkRegion() {
			return targetRegion;
		}
	}

	private ExperimentMultiPageEditor adaptable;

	YamlHyperlinkDetector(ExperimentMultiPageEditor editor) {
		this.adaptable = editor;
	}
	
	public static class FeatureInfo{
		public ArrayList<InstrospectedFeature>fs=new ArrayList<>();
		public final int offsetLeft;
		public final String functionName;
		public FeatureInfo(ArrayList<InstrospectedFeature> fs, int offsetLeft, String functionName) {
			super();
			this.fs = fs;
			this.offsetLeft = offsetLeft;
			this.functionName = functionName;
		}
	}

	public static FeatureInfo detectFeatures(ExperimentMultiPageEditor adaptable,
			ITextViewer textViewer, IRegion region) {
		if (adaptable == null) {
			return null;
		}

		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(offset);
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		int offsetInLine = offset - lineInfo.getOffset();
		String leftChars = line.substring(0, offsetInLine);
		String rightChars = line.substring(offsetInLine);
		StringBuilder sb = new StringBuilder();
		int offsetLeft = offset;
		char[] left = leftChars.toCharArray();
		for (int i = left.length - 1; i >= 0; i--) {
			char c = left[i];
			if (Character.isWhitespace(c)) {
				break;
			}
			if (c == ',') {
				break;
			}
			if (c == '[') {
				break;
			}
			if (c == ']') {
				break;
			}
			if (c == ':') {
				break;
			}
			offsetLeft--;
			sb.insert(0, c);
		}
		for (char c : rightChars.toCharArray()) {
			if (Character.isWhitespace(c)) {
				break;
			}
			if (c == ',') {
				break;
			}
			if (c == '[') {
				break;
			}
			if (c == ':') {
				break;
			}
			if (c == ']') {
				break;
			}
			sb.append(c);
		}
		String functionName = sb.toString();
		if (functionName.startsWith(":") && functionName.length() > 1) {
			// handle goto :xxx like goto xxx
			functionName = functionName.substring(1);
		}
		ArrayList<InstrospectedFeature> ls = new ArrayList<>();
		LinkedHashSet<String> sss = new LinkedHashSet<>();
		ProjectWrapper project = adaptable.getProject();
		if (project != null) {
			InstrospectionResult details = project.getDetails();
			if (details != null) {
				ArrayList<InstrospectedFeature> features = details.getFeatures();
				for (InstrospectedFeature f : features) {

					if (f.getName().equalsIgnoreCase(functionName)
							|| ("val_" + f.getName()).equalsIgnoreCase(functionName)) {
						if (!sss.add(f.getName() + f.getSourcefile())) {
							continue;
						}
						ls.add(f);
					}
				}
			}
		}
		
		return new FeatureInfo(ls, offsetLeft, functionName);
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		FeatureInfo detectFeatures2 = detectFeatures(adaptable, textViewer, region);
		ArrayList<InstrospectedFeature> detectFeatures = detectFeatures2.fs;
		ArrayList<IHyperlink> ls = new ArrayList<>();
		for (InstrospectedFeature f : detectFeatures) {
			Region targetRegion = new Region(detectFeatures2.offsetLeft, detectFeatures2.functionName.length());
			ls.add(new MusketHyperLink(f, targetRegion));
		}
		if (ls.isEmpty()) {
			return null;
		}
		return ls.toArray(new IHyperlink[ls.size()]);
	}

}
