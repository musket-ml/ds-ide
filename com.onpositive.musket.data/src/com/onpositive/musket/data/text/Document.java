package com.onpositive.musket.data.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.generic.StringUtils;
import com.onpositive.musket.data.images.IImageItem;

public class Document implements ITextItem, IImageItem {

	protected ArrayList<Sentence> contents = new ArrayList<>();

	protected TextSequenceDataSet parent;

	private int num;

	public Document(TextSequenceDataSet parent, int num) {
		super();
		this.parent = parent;
		this.num = num;
	}

	public boolean isEmpty() {
		return this.contents.isEmpty();
	}

	public void add(Sentence curSeq) {
		this.contents.add(curSeq);
	}

	@Override
	public String toString() {
		return this.contents.stream().map(x -> x.toString()).collect(Collectors.joining(System.lineSeparator()));
	}

	@Override
	public String id() {
		return "" + num;
	}

	@Override
	public IDataSet getDataSet() {
		return parent;
	}

	@Override
	public String getText() {
		return toString();
	}

	protected static HashMap<Integer, Font> fonts = new HashMap<>();

	protected String getTextValue(int mxch) {
		String value = getText();
		if (value.length() > mxch) {
			value = value.substring(0, mxch) + "...";
		}
		return value;
	}

	@Override
	public Image getImage() {
		BufferedImage img = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setColor(Color.RED);

		int fs = 12;
		int mxch = 300;
		Font font = javax.swing.UIManager.getDefaults().getFont("TextArea.font");
		try {
			fs = Integer.parseInt(parent.getSettings().get(GenericDataSet.FONT_SIZE).toString());
			mxch = Integer.parseInt(parent.getSettings().get(GenericDataSet.MAX_CHARS_IN_TEXT).toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (!fonts.containsKey(fs)) {
			font = new Font(font.getFamily(), font.getStyle(), fs);
			fonts.put(fs, font);
		} else {
			font = fonts.get(fs);
		}

		JTextPane area = new JTextPane();
		area.setContentType("text/html");
		// area.setLineWrap(true);
		area.setFont(font);
		area.setBorder(new TitledBorder("Text"));
		BufferedImage nn = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
		area.setLocation(0, 0);
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = new HTMLDocument();
		area.setEditorKit(kit);
		area.setDocument(doc);
		ClassVisibilityOptions visibility = this.parent.getVisibility();
		try {
			StringBuilder bld = new StringBuilder();
			bld.append("<font size='4'>");
			drawText(mxch, visibility, bld);
			bld.append("</font>");
			kit.insertHTML(doc, doc.getLength(), bld.toString(), 0, 0, null);

		} catch (BadLocationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		area.setSize(350, 350);
		area.paint(nn.getGraphics());

		g2.drawImage(nn, 0, 0, null);

		return img;

	}

	protected void drawText(int mxch, ClassVisibilityOptions visibility, StringBuilder bld) {
		l2:for (Sentence s : this.contents) {
			bld.append("<p>");
			for (Token t : s.tokens) {
				
				List<String> classes = t.classes();
				classes=visibility.filter(classes);					
				if (!classes.isEmpty()&&(visibility.showInText||classes.size()>1)) {
					bld.append(StringUtils.encodeHtml(t.toString()+" "));
					bld.append("<font color='blue'>");
					bld.append('(');
					
					bld.append(classes.stream().collect(Collectors.joining(", ")));
					bld.append(") ");
					bld.append("</font>");
				}
				else {
					if (classes.size()>0) {
					Color color = visibility.getColor(classes.get(0));
						bld.append("<b><font color='#"+Integer.toHexString(color.getRGB()).substring(2)+"'>");
						bld.append(StringUtils.encodeHtml(t.toString())+" ");
						bld.append("</font></b>");
					}
					else {
						bld.append(StringUtils.encodeHtml(t.toString()+" "));
					}
				}
				if (bld.length()>mxch) {
					bld.append("...");
					break l2;
				}
			}
			bld.append("</p>");
		}
	}


	@Override
	public void drawOverlay(Image image, int color) {

	}

	@Override
	public Point getImageDimensions() {
		return new Point(350, 350);
	}

	public ArrayList<LinkedHashSet<String>>classes(){
		ArrayList<LinkedHashSet<String>>classes=new ArrayList<>();
		this.contents.forEach(v->{
			ArrayList<LinkedHashSet<String>> tclasses = v.classes();
			for (int i=0;i<tclasses.size();i++) {
				LinkedHashSet<String> string = tclasses.get(i);
				if (classes.size()<=i) {
					classes.add(new LinkedHashSet<>());
				}
				classes.get(i).addAll(string);
			}
		});
		return classes;
	}
}
