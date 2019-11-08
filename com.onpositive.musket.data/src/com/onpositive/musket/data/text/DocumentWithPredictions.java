package com.onpositive.musket.data.text;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.musket.data.generic.StringUtils;

public class DocumentWithPredictions extends Document {

	private Document prediction;

	public DocumentWithPredictions(TextSequenceDataSetWithPredictions parent, Document rs, Document iItem) {
		super(parent, Integer.parseInt(rs.id()));
		this.prediction = iItem;
		this.contents.addAll(rs.contents);
	}
	
	public boolean allMatches() {
		return this.contents.equals(prediction.contents);
	}

	protected void drawText(int mxch, ClassVisibilityOptions visibility, StringBuilder bld) {
		int num = 0;
		l2: for (Sentence s : this.contents) {
			bld.append("<p>");
			if (num >= prediction.contents.size()) {
				bld.append("Sentence does not matched...</p>");
				break;
			}
			Sentence sentence = prediction.contents.get(num);
			int j = 0;
			for (Token t : s.tokens) {
				if (j >= s.tokens.size()) {
					bld.append("Sentence does not matched...</p>");
					break l2;
				}
				Token token = sentence.tokens.get(j);
				if (!t.toString().equals(token.toString())) {
					bld.append("Tokens does not matched...</p>");
					break l2;
				}
				j++;
				List<String> classes = t.classes();

				List<String> classes2 = token.classes();

				classes = visibility.filter(classes);
				classes2 = visibility.filter(classes2);
				if (this.parent.settings.get(TextSequenceDataSetWithPredictions.FOCUS_ON_DIFFERENCES).toString()
						.equals("true") && classes2.equals(classes)) {
					bld.append(StringUtils.encodeHtml(token.toString() + " "));
				} else {
					
					if (!classes2.equals(classes)) {
						if (visibility.showInText) {
							bld.append("<b>");
							bld.append(StringUtils.encodeHtml(t.toString() + " "));
							bld.append("<font color='blue'>");
							bld.append('(');

							bld.append(classes.stream().collect(Collectors.joining(", ")));
							bld.append("|");
							bld.append(classes2.stream().collect(Collectors.joining(", ")));
							bld.append(") ");
							bld.append("</font>");
							bld.append("</b>");
						} else {
							appendToken(visibility, bld, t, classes);
							bld.append("|");
							appendToken(visibility, bld, t, classes2);
						}
					}
					else {
						appendToken(visibility, bld, t, classes);
					}
				}
				if (bld.length() > mxch) {
					bld.append("...");
					break l2;
				}
			}
			num++;
			bld.append("</p>");
		}
	}

	protected void appendToken(ClassVisibilityOptions visibility, StringBuilder bld, Token t, List<String> classes) {
		if (!classes.isEmpty() && (visibility.showInText || classes.size() > 1)) {
			bld.append(StringUtils.encodeHtml(t.toString() + " "));
			bld.append("<font color='blue'>");
			bld.append('(');

			bld.append(classes.stream().collect(Collectors.joining(", ")));
			bld.append(") ");
			bld.append("</font>");
		} else {
			if (classes.size() > 0) {
				Color color = visibility.getColor(classes.get(0));
				bld.append("<b><font color='#" + Integer.toHexString(color.getRGB()).substring(2) + "'>");
				bld.append(StringUtils.encodeHtml(t.toString()) + " ");
				bld.append("</font></b>");
			} else {
				bld.append(StringUtils.encodeHtml(t.toString() + " "));
			}
		}
	}
}
