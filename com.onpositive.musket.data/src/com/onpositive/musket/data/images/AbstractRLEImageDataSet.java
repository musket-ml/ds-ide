package com.onpositive.musket.data.images;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageDataSetFactories;
import com.onpositive.musket.data.table.ImageRepresenter;
import com.onpositive.musket.data.text.AbstractImageItem;

public abstract class AbstractRLEImageDataSet<T extends IImageItem> extends AbstractImageDataSet<T>
		implements IImageDataSet, Cloneable {

	public static final String CLAZZ = "CLASS";
	private static final String RELATIVE_RLE = "RELATIVE_RLE";

	private static final String RLE_COLUMN = "rle_column";
	private static final String WIDTH_FIRST = "width_first";
	private static final String MASK_IS_SAME_AS_IMAGE = "mask_is_same_as_image";

	{
		parameters.put(MASK_ALPHA, MASK_ALPHA_DEFAULT);
		parameters.put(MASK_COLOR, MASK_COLOR_DEFAULT);
	}

	public AbstractRLEImageDataSet(DataSetSpec spec, IColumn image, IColumn rle, int width2, int height2) {
		super(spec.tabularOrigin(), image, width2, height2, spec.getRepresenter());
		this.tabularBase = spec.tabularOrigin();
		this.imageColumn = image;
		this.rleColumn = rle;
		if (this.isMultiResolution) {
			MaskModel model = new MaskModel();
			boolean askQuestion = spec.answerer.askQuestion("Mask size", model);
			if (!askQuestion) {
				throw new NotEnoughParametersException("Can not determine mask resolution");
			} else {
				width2 = model.height;
				height2 = model.width;
				maskIsSameAsImage = model.sameAsImage;
				getSettings().put(WIDTH, width2);
				getSettings().put(HEIGHT, height2);

			}
		}
		this.width = width2;
		this.height = height2;
		this.representer = spec.getRepresenter();
		this.isRelativeRLE = false;
		this.widthFirst = true;

		getSettings().put(RLE_COLUMN, rle.id());
		getSettings().put(MASK_IS_SAME_AS_IMAGE, maskIsSameAsImage);

		// here we should load from meta if we can!!!!;
		if (!checkValidity(false, true)) {
			if (!checkValidity(false, false)) {
				if (!checkValidity(true, false)) {
					if (!checkValidity(true, false)) {
						throw new IllegalStateException();
					}
				}
			}
		}
		getSettings().put(RELATIVE_RLE, isRelativeRLE);
		getSettings().put(WIDTH_FIRST, widthFirst);
		getSettings().put(WIDTH_FIRST, widthFirst);

	}

	protected boolean maskIsSameAsImage = false;

	public AbstractRLEImageDataSet(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
		this.rleColumn = base.getColumn((String) settings.get(RLE_COLUMN));
		this.isRelativeRLE = Boolean.parseBoolean(settings.get(RELATIVE_RLE).toString());
		this.widthFirst = Boolean.parseBoolean(settings.get(WIDTH_FIRST).toString());
		Object object = settings.get(MASK_IS_SAME_AS_IMAGE);
		if (object == null) {
			object = "false";
		}
		this.maskIsSameAsImage = Boolean.parseBoolean(object.toString());
	}

	private boolean checkValidity(boolean b, boolean c) {
		this.isRelativeRLE = b;
		this.widthFirst = c;
		try {

			Optional<? extends ITabularItem> findAny = this.tabularBase.items().parallelStream().filter(v -> {
				@SuppressWarnings("rawtypes")
				RLEMask createMask = (RLEMask) this.createMask(this.rleColumn.getValueAsString(v), this.width,
						this.height, new AbstractImageItem<AbstractRLEImageDataSet>(this) {

							@Override
							public String id() {
								return v.id();
							}

							

							@Override
							public Image getImage() {
								return representer.get(imageColumn.getValueAsString(v));
							}

							@Override
							public void drawOverlay(Image image, int color) {

							}

							@Override
							public Point getImageDimensions() {
								return representer.getDimensions(imageColumn.getValueAsString(v));
							}
						});
				if (!createMask.checkValid()) {
					return true;
				}
				return false;

			}).findAny();
			if (findAny.isPresent()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	protected boolean widthFirst;

	protected IColumn rleColumn;

	protected boolean isRelativeRLE = true;

	public RLEMask createMask(String valueAsString, int height, int width, IImageItem item) {
		valueAsString = valueAsString.trim();
		if (this.maskIsSameAsImage) {
			Point p = item.getImageDimensions();
			height = p.x;
			width = p.y;
		}
		if (widthFirst) {
			if (isRelativeRLE) {
				return new RelativeRLEMask(valueAsString, width, height);
			}
			return new RLEMask(valueAsString, width, height);
		} else {
			if (isRelativeRLE) {
				return new RelativeRLEMask(valueAsString, height, width);
			}
			return new RLEMask(valueAsString, height, width);
		}
	}

	public static String MASK_COLOR = "Mask color";
	public static String MASK_ALPHA = "Mask alpha";

	public static String MASK_COLOR_DEFAULT = "255,0,0";
	public static String MASK_ALPHA_DEFAULT = "255";

	public static int parse(String alpha, String string2) {
		int indexOf = string2.indexOf("{");
		if (indexOf != -1) {
			string2 = string2.substring(indexOf + 1, string2.length() - 1);
		}
		String[] rr = string2.split(",");
		Color color = new Color(Integer.parseInt(rr[0].trim()), Integer.parseInt(rr[1].trim()),
				Integer.parseInt(rr[2].trim()), Integer.parseInt(alpha.trim()));
		return color.getRGB();
	}

	@Override
	public IVisualizerProto getVisualizer() {
		return new IVisualizerProto() {

			@Override
			public Parameter[] parameters() {
				Parameter alpha = new Parameter();
				alpha.defaultValue = parameters.get(MASK_ALPHA).toString();
				alpha.type = int.class;
				alpha.name = MASK_ALPHA;

				Parameter nk = new Parameter();
				nk.defaultValue = parameters.get(MASK_COLOR).toString();
				nk.type = Color.class;
				nk.name = MASK_COLOR;
				return new Parameter[] { alpha, nk };
			}

			@Override
			public String name() {
				return "Image visualizer";
			}

			@Override
			public String id() {
				return "Image visualizer";
			}

			@Override
			public Supplier<Collection<String>> values(IDataSet ds) {
				return null;
			}
		};
	}

	protected void fillDescription(ArrayList<DescriptionEntry> entries) {
		super.fillDescription(entries);
		entries.add(new DescriptionEntry("Mask encoding", this.maskEncoding()));
	}

	protected String getRLEColumn() {
		return this.rleColumn.caption();
	}

	private String maskEncoding() {
		String result = "";
		if (this.isRelativeRLE) {
			result += "Relative RLE";
		} else {
			result += "RLE";
		}
		if (this.widthFirst) {
			if (!result.isEmpty()) {
				result += " ";
			}
			result += "Width first";
		}
		return result;
	}

}