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
import com.onpositive.musket.data.generic.StringUtils;
import com.onpositive.musket.data.images.IBinaryClasificationItem;
import com.onpositive.musket.data.images.IImageItem;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.musket.data.images.MultiClassClassificationItem;
import com.onpositive.musket.data.table.ITabularItem;

public class TextItem implements ITextItem, IBinaryClasificationItem, IMulticlassClassificationItem, IImageItem {

	protected AbstractTextDataSet textDataSet;
	protected ITabularItem baseItem;

	public TextItem(AbstractTextDataSet textDataSet, ITabularItem baseItem) {
		super();
		this.textDataSet = textDataSet;
		this.baseItem = baseItem;
	}

	@Override
	public String id() {
		if (textDataSet.idColumn != null) {
			return textDataSet.idColumn.getValueAsString(this.baseItem);
		}
		return this.baseItem.id();
	}

	@Override
	public IDataSet getDataSet() {
		return textDataSet;
	}

	protected static HashMap<Integer, Font> fonts = new HashMap<>();

	protected String getTextValue(int mxch) {
		String value = getText();
		if (value.length() > mxch) {
			value = value.substring(0, mxch) + "...";
		}
		return StringUtils.encodeHtml(value);
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
			fs = Integer.parseInt(textDataSet.getSettings().get(GenericDataSet.FONT_SIZE).toString());
			mxch = Integer.parseInt(textDataSet.getSettings().get(GenericDataSet.MAX_CHARS_IN_TEXT).toString());
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

	protected String getClassText() {
		ArrayList<String> classes = this.classes();
		String string = "<html>" + StringUtils.encodeHtml(classes.stream().collect(Collectors.joining(", "))) + "</html>";
		return string;
	}

	@Override
	public String getText() {
		return textDataSet.textColumn.getValueAsString(baseItem);
	}

	public Object binaryLabel() {
		return textDataSet.binaryLabel(this);
	}

	@Override
	public boolean isPositive() {
		return textDataSet.isPositive(this);
	}

	@Override
	public ArrayList<String> classes() {
		String value = (String) ((TextClassificationDataSet) this.textDataSet).clazzColumn.getValue(baseItem);
		return MultiClassClassificationItem.splitByClass(value, this.textDataSet.labels);
	}

	@Override
	public void drawOverlay(Image image, int color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point getImageDimensions() {
		// TODO Auto-generated method stub
		return null;
	}

}
