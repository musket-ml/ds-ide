package com.onpositive.musket.data.generic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.ImageColumnType;
import com.onpositive.musket.data.columntypes.NumberColumn;
import com.onpositive.musket.data.columntypes.RLEMaskColumnType;
import com.onpositive.musket.data.columntypes.TextColumnType;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.IImageItem;
import com.onpositive.musket.data.images.RLEMask;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.ITabularItem;

public class GenericItem implements IImageItem {

	protected GenericDataSet ds;
	protected ITabularItem base;

	public ITabularItem getBase() {
		return base;
	}

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

	protected static HashMap<Integer, Font> fonts = new HashMap<>();

	@Override
	public Image getImage() {
		BufferedImage img = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setColor(Color.RED);

		DataSetSpec spec = this.ds.getSpec();
		int fs = 12;
		int mxch = 300;
		Font font = javax.swing.UIManager.getDefaults().getFont("TextArea.font");
		try {
			fs = Integer.parseInt(ds.getSettings().get(GenericDataSet.FONT_SIZE).toString());
			mxch = Integer.parseInt(ds.getSettings().get(GenericDataSet.MAX_CHARS_IN_TEXT).toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (!fonts.containsKey(fs)) {
			font = new Font(font.getFamily(), font.getStyle(), fs);
			fonts.put(fs, font);
		} else {
			font = fonts.get(fs);
		}
		Collection<ColumnInfo> infos = spec.layout.infos();
		Map<String, Object> settings = ds.getSettings();
		ArrayList<ColumnInfo> large = new ArrayList<>();
		Object object = settings.get(GenericDataSet.VISIBLE_COLUMNS);
		String vs = object == null ? "" : object.toString();
		if (!vs.isEmpty()) {
			String[] split = vs.split(",");
			HashSet<String> hashSet = new HashSet<>(Arrays.asList(split));
			ArrayList<ColumnInfo> selected = new ArrayList<>();
			for (ColumnInfo i : infos) {
				if (hashSet.contains(i.getColumn().caption())) {
					selected.add(i);
				}
			}
			infos = selected;
		}
		StringBuilder bld = new StringBuilder();
		ArrayList<ColumnInfo> rles = new ArrayList<>();
		for (ColumnInfo i : infos) {

			Class<? extends IColumnType> preferredType = i.preferredType();
			boolean isRle = preferredType == RLEMaskColumnType.class;
			if (preferredType == TextColumnType.class || preferredType == ImageColumnType.class || isRle) {
				if (isRle) {
					rles.add(i);
					continue;
				}
				large.add(i);
			}
		}
		for (ColumnInfo i : infos) {
			IColumn column = i.getColumn();
			Class<? extends IColumnType> preferredType = i.preferredType();
			
			if (preferredType == TextColumnType.class || preferredType == ImageColumnType.class
					|| preferredType == RLEMaskColumnType.class) {

				continue;
			} else {
				// this is small thing
				String title = column.caption();

				
				if (large.isEmpty()) {
					bld.append("<li><bold>" + StringUtils.encodeHtml(title) + ": </bold>");
				} else {
					bld.append(StringUtils.encodeHtml(title) + ": ");
				}
				appendSimpleValue(bld, column, preferredType);
			}
		}

		JLabel label = new JLabel("<html>" + bld.toString() + "</html>");
		Border createEmptyBorder = BorderFactory.createEmptyBorder(5, 5, 0, 5);

		TitledBorder titledBorder = new TitledBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createTitledBorder("").getBorder(), createEmptyBorder));

		titledBorder.setTitle("Simple properties");
		label.setBorder(titledBorder);
		label.setOpaque(true);

		label.setFont(font);
		label.setSize(350, 350);
		Dimension preferredSize = label.getPreferredSize();
		int pos = preferredSize.height;
		if (pos > 100 && large.size() > 0) {
			pos = 100;
		}
		if (preferredSize.width>400) {
			pos = 100;
		}
		if (large.isEmpty()) {
			pos = 300;
		}

		label.setLocation(0, 0);
		label.setSize(350, pos);
		label.paint(g2);
		if (large.size() > 0) {
			int height = (350 - pos - 10) / large.size();
			int num = 0;
			for (ColumnInfo i : large) {
				IColumn column = i.getColumn();
				Class<? extends IColumnType> preferredType = i.preferredType();
				
				if (preferredType == TextColumnType.class) {
					JTextArea area = new JTextArea();
					area.setWrapStyleWord(true);
					area.setLineWrap(true);
					area.setFont(font);
					area.setBorder(new TitledBorder(column.caption()));
					String value = getTextValue(mxch, column);
					BufferedImage nn = new BufferedImage(350, height, BufferedImage.TYPE_INT_ARGB);
					area.setLocation(0, height * num);
					area.setText(value);
					area.setSize(350, height);
					area.paint(nn.getGraphics());

					g2.drawImage(nn, 0, height * num + pos + 10, null);
					// g2.transform(AffineTransform.getTranslateInstance(0, height*num));
					num++;
				}
				if (preferredType == ImageColumnType.class) {
					String valueAsString = column.getValueAsString(base);
					BufferedImage bufferedImage = ds.getSpec().representer.get(valueAsString);
					int width = bufferedImage.getWidth();
					int height2 = bufferedImage.getHeight();
					double sw = width / 350.0;
					double sh = height2 / height;
					double scale = Math.max(sw, sh);
					g2.drawImage(bufferedImage, 0, height * num + pos + 10, (int) (width / scale),
							(int) (height2 / scale), null);
					int nm = num;
					int ps = pos;
					rles.forEach(r -> {
						String rleMask = r.getColumn().getValueAsString(base);
						try {
							RLEMask ms = new RLEMask(rleMask, height2, width);
							Image image = ms.getImage();
							g2.drawImage(image, 0, height * nm + ps + 10, (int) (width / scale),
									(int) (height2 / scale), null);
						} catch (Exception e) {
							// TODO: handle exception
						}
					});
				}
			}
		}
		return img;
	}

	protected String getTextValue(int mxch, IColumn column) {
		String value = column.getValueAsString(base);
		if (value.length() > mxch) {
			value = value.substring(0, mxch) + "...";
		}
		return value;
	}

	protected void appendSimpleValue(StringBuilder bld, IColumn column, Class<? extends IColumnType> preferredType) {
		String value = column.getValueAsString(base);
		if (preferredType == NumberColumn.class) {
			try {
				value = NumberFormat.getInstance().format(Double.parseDouble(value));
			} catch (NumberFormatException e) {
			}
		}
		if (value.length() > 100) {
			value = value.substring(0, 100);
		}
		bld.append("<FONT COLOR=BLUE>" + StringUtils.encodeHtml(value) + "</FONT>");
		bld.append(" ");
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
