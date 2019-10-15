package com.onpositive.musket.data.generic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.ImageColumnType;
import com.onpositive.musket.data.columntypes.NumberColumn;
import com.onpositive.musket.data.columntypes.RLEMaskColumnType;
import com.onpositive.musket.data.columntypes.TextColumnType;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.IImageItem;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.ITabularItem;

public class GenericItem implements IImageItem {

	protected GenericDataSet ds;
	protected ITabularItem base;

	public GenericItem(GenericDataSet ds, ITabularItem base) {
		super();
		this.ds = ds;
		this.base = base;
	}

	@Override
	public String id() {
		return base.id();
	}

	@Override
	public IDataSet getDataSet() {
		return ds;
	}

	@Override
	public Image getImage() {
		BufferedImage img = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setColor(Color.RED);

		DataSetSpec spec = this.ds.getSpec();

		Collection<ColumnInfo> infos = spec.layout.infos();

		ArrayList<ColumnInfo> large = new ArrayList<>();
		StringBuilder bld = new StringBuilder();
		for (ColumnInfo i : infos) {
			IColumn column = i.getColumn();
			Class<? extends IColumnType> preferredType = i.preferredType();
			String value = column.getValueAsString(base);
			if (preferredType == TextColumnType.class || preferredType == ImageColumnType.class
					|| preferredType == RLEMaskColumnType.class) {
				large.add(i);
				if (preferredType == TextColumnType.class) {
					if (value.length() > 500) {
						value = value.substring(0, 500) + "...";
					}
				}

			}

		}
		for (ColumnInfo i : infos) {
			IColumn column = i.getColumn();
			Class<? extends IColumnType> preferredType = i.preferredType();
			String value = column.getValueAsString(base);
			if (preferredType == TextColumnType.class || preferredType == ImageColumnType.class
					|| preferredType == RLEMaskColumnType.class) {

				continue;
			} else {
				// this is small thing
				String title = column.caption();

				if (preferredType == NumberColumn.class) {
					try {
						value = NumberFormat.getInstance().format(Double.parseDouble(value));
					} catch (NumberFormatException e) {
					}
				}
				if (large.isEmpty()) {
					bld.append("<li><bold>" + StringUtils.encodeHtml(title) + ": </bold>");
				} else {
					bld.append(StringUtils.encodeHtml(title) + ": ");
				}
				if (value.length() > 100) {
					value = value.substring(0, 100);
				}
				bld.append("<FONT COLOR=BLUE>" + StringUtils.encodeHtml(value) + "</FONT>");
				bld.append(" ");
			}
		}
		JLabel label = new JLabel("<html>" + bld.toString() + "</html>");
		TitledBorder titledBorder = new TitledBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("").getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		titledBorder.setTitle("Simple properties");
		label.setBorder(titledBorder);
		label.setFont(javax.swing.UIManager.getDefaults().getFont("TextArea.font"));
		label.setSize(350, 350);
		Dimension preferredSize = label.getPreferredSize();
		int pos = preferredSize.height;
		if (pos > 100&&large.size()>0) {
			pos = 100;
		}
		label.setLocation(0, pos);
		label.setSize(350, pos);
		label.paint(g2);
		if (large.size() > 0) {
			int height = (350 - pos - 10) / large.size();
			int num = 0;
			for (ColumnInfo i : large) {
				IColumn column = i.getColumn();
				Class<? extends IColumnType> preferredType = i.preferredType();
				String value = column.getValueAsString(base);
				if (preferredType == TextColumnType.class) {
					JTextArea area = new JTextArea();
					area.setWrapStyleWord(true);
					area.setLineWrap(true);
					area.setBorder(new TitledBorder(column.caption()));
					if (value.length() > 300) {
						value = value.substring(0, 300) + "...";
					}
					BufferedImage nn = new BufferedImage(350, height, BufferedImage.TYPE_INT_ARGB);
					area.setLocation(0, height * num);
					area.setText(value);
					area.setSize(350, height);
					area.paint(nn.getGraphics());

					g2.drawImage(nn, 0, height * num + pos + 10, null);
					// g2.transform(AffineTransform.getTranslateInstance(0, height*num));
					num++;
				}
			}
		}
		return img;
	}

	@Override
	public void drawOverlay(Image image, int color) {

	}

	@Override
	public Point getImageDimensions() {
		return new Point(350, 350);
	}

	public ITabularItem base() {
		return base;
	}

}
