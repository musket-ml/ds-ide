package com.onpositive.musket.data.images;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class MultiClassSegmentationDataSet extends AbstractRLEImageDataSet<IImageItem>
		implements IMultiClassSegmentationDataSet,IBinarySegmentationDataSet {

	public static final String CLAZZ_COLUMN = "CLAZZ_COLUMN";
	protected IColumn clazzColumn;
	protected ArrayList<Object> classes;

	@SuppressWarnings("unchecked")
	public MultiClassSegmentationDataSet(ITabularDataSet base2, IColumn image, IColumn rle, int width2, int height2,
			ImageRepresenter rep, IColumn clazzColumn) {
		super(base2, image, rle, width2, height2, rep);
		this.clazzColumn = clazzColumn;
		this.getSettings().put(CLAZZ_COLUMN, this.clazzColumn.id());
		Collection<Object> values = clazzColumn.values();
		classes = new ArrayList(new LinkedHashSet<>(values));
		try {
			Collections.sort((List) classes);
		} catch (Exception e) {
		}
		if (classes.size() < 10) {
			int num = 0;
			Color[] classesC = new Color[] {  Color.CYAN, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW,
					Color.DARK_GRAY, Color.PINK, Color.ORANGE, Color.MAGENTA ,Color.BLACK,Color.WHITE};
			for (Color f : classesC) {

				Color object = f;
				String vl = object.getRed() + "," + object.getGreen() + "," + object.getBlue();
				parameters.put(getClassMaskKey(classes.get(num)), vl);
				num = num + 1;
				if (num >= classes.size()) {
					break;
				}

			}
		}

	}

	public MultiClassSegmentationDataSet(ITabularDataSet base, Map<String, Object> settings, ImageRepresenter rep) {
		super(base, settings, rep);
		this.clazzColumn=base.getColumn(settings.get(CLAZZ_COLUMN).toString());
		Collection<Object> values = clazzColumn.values();
		classes = new ArrayList(new LinkedHashSet<>(values));
		try {
			Collections.sort((List) classes);
		} catch (Exception e) {
		}
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		return null;
	}

	@Override
	public boolean isExclusive() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> classNames() {
		return (List)classes;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<IMultiClassSegmentationItem> items() {
		if (items == null) {
			items = new ArrayList<>();
			LinkedHashMap<String, ArrayList<ITabularItem>> items = new LinkedHashMap<>();
			base.items().forEach(v -> {
				String value = imageColumn.getValueAsString(v);
				ArrayList<ITabularItem> arrayList = items.get(value);
				if (arrayList == null) {
					arrayList = new ArrayList<>();
					items.put(value, arrayList);
				}
				arrayList.add(v);

			});
			items.keySet().forEach(k -> {
				MultiClassSegmentationItem bi = new MultiClassSegmentationItem(k, this, items.get(k));
				this.items.add(bi);
			});
		}
		return (List) items;
	}

	{

	}

	public String getClassMaskKey(Object num) {
		return "Class " + num + " mask";
	}

	@Override
	public IVisualizerProto getVisualizer() {
		return new IVisualizerProto() {

			@Override
			public Parameter[] parameters() {
				ArrayList<Parameter> rs = new ArrayList<>();
				Parameter alpha = new Parameter();
				alpha.defaultValue = parameters.get(MASK_ALPHA).toString();
				alpha.type = int.class;
				alpha.name = MASK_ALPHA;
				rs.add(0, alpha);

				if (classes.size() < 10) {
					for (Object o : classes) {
						String classMaskKey = getClassMaskKey(o);
						Parameter nk = new Parameter();
						nk.defaultValue = parameters.get(classMaskKey).toString();
						nk.type = Color.class;
						nk.name = classMaskKey;
						rs.add(nk);
					}
				} else {

					Parameter nk = new Parameter();
					nk.defaultValue = parameters.get(MASK_COLOR).toString();
					nk.type = Color.class;
					nk.name = MASK_COLOR;
					rs.add(nk);
				}
				return rs.toArray(new Parameter[rs.size()]);
			}

			@Override
			public String name() {
				return "Image visualizer";
			}

			@Override
			public String id() {
				return "Image visualizer";
			}
		};
	}

	@Override
	protected String getKind() {
		return "Multi class segmentation";
	}

}