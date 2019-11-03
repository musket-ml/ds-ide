package com.onpositive.musket.data.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.images.IImageItem;

public class Document implements ITextItem,IImageItem{

	protected ArrayList<Sentence>contents=new ArrayList<>();

	protected TextSequenceDataSet parent;

	private int num;
	
	public Document(TextSequenceDataSet parent,int num) {
		super();
		this.parent = parent;
		this.num=num;
	}

	public boolean isEmpty() {
		return this.contents.isEmpty();
	}

	public void add(Sentence curSeq) {
		this.contents.add(curSeq);
	}
	
	@Override
	public String toString() {
		return this.contents.stream().map(x->x.toString()).collect(Collectors.joining(System.lineSeparator()));
	}

	@Override
	public String id() {
		return ""+num;
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

		JTextArea area = new JTextArea();
		area.setWrapStyleWord(true);
		area.setLineWrap(true);
		area.setFont(font);
		area.setBorder(new TitledBorder("Text"));
		String value = getTextValue(mxch);
		BufferedImage nn = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
		area.setLocation(0, 0);
		area.setText(value);
		area.setSize(350, 310);
		area.paint(nn.getGraphics());

		g2.drawImage(nn, 0, 40, null);
		
		String string = getClassText();
		JLabel label = new JLabel(string);
		Border createEmptyBorder = BorderFactory.createEmptyBorder(5, 5, 0, 5);

		TitledBorder titledBorder = new TitledBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createTitledBorder("").getBorder(), createEmptyBorder));

		titledBorder.setTitle("Classes");
		label.setBorder(titledBorder);
		label.setSize(350, 40);
		label.paint(g2);
		return img;

	}

	private String getClassText() {
		return "";
	}

	@Override
	public void drawOverlay(Image image, int color) {
		
	}

	@Override
	public Point getImageDimensions() {
		return new Point(350,350);
	}
}
