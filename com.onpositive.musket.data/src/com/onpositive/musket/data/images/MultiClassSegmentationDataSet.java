package com.onpositive.musket.data.images;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IHasLabels;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;
import com.onpositive.musket.data.text.ClassVisibilityOptions2;
import com.onpositive.musket.data.text.IHasClassGroups;

public class MultiClassSegmentationDataSet extends AbstractRLEImageDataSet<IImageItem>
		implements IMultiClassSegmentationDataSet,IBinarySegmentationDataSet ,IHasLabels, IHasClassGroups {

	public static final String CLAZZ_COLUMN = "CLAZZ_COLUMN";
	protected IColumn clazzColumn;
	protected List<Object> classes;
	
	public static String FOCUS_ON_TARGET_CLASS = "Focus on target class";
	public static String CLASSES_COLOURS = "Classes colours";
	public static boolean FOCUS_ON_TARGET_CLASS_DEFAULT = true;
	
	{
		parameters.put(FOCUS_ON_TARGET_CLASS, FOCUS_ON_TARGET_CLASS_DEFAULT);
	}

	@SuppressWarnings("unchecked")
	public MultiClassSegmentationDataSet(DataSetSpec base, IColumn image, IColumn rle, int width2, int height2, IColumn clazzColumn) {
		super(base, image, rle, width2, height2);
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
		return new MultiClassSegmentationDataSetWithGrounTruth(new DataSetSpec(tabularBase, representer),imageColumn,rleColumn,width,height,clazzColumn,t2.as(ITabularDataSet.class));
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
			tabularBase.items().forEach(v -> {
				String value = imageColumn.getValueAsString(v);
				ArrayList<ITabularItem> arrayList = items.get(value);
				if (arrayList == null) {
					arrayList = new ArrayList<>();
					items.put(value, arrayList);
				}
				arrayList.add(v);

			});
			items.keySet().forEach(k -> {
				MultiClassSegmentationItem bi = createItem(items, k);
				this.items.add(bi);
			});
		}
		return (List) items;
	}

	protected MultiClassSegmentationItem createItem(LinkedHashMap<String, ArrayList<ITabularItem>> items, String k) {
		return new MultiClassSegmentationItem(k, this, items.get(k));
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
					
					Parameter parameter2 = new Parameter();
					parameter2.name=CLASSES_COLOURS;
					Object object = MultiClassSegmentationDataSet.this.getSettings().get(CLASSES_COLOURS);
					if (object==null) {
						object="";
					}
					parameter2.defaultValue=object.toString();
					parameter2.type=ClassVisibilityOptions2.class;
					rs.add(parameter2);
				}
				
				Parameter focus = new Parameter();
				focus.defaultValue = parameters.get(FOCUS_ON_TARGET_CLASS).toString();
				focus.type = boolean.class;
				focus.name = FOCUS_ON_TARGET_CLASS;
				rs.add(focus);
				addExtraParameters(rs);
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

			@Override
			public Supplier<Collection<String>> values(IDataSet ds) {
				return null;
			}
		};
	}

	protected void addExtraParameters(ArrayList<Parameter> rs) {
		
	}

	@Override
	protected String getKind() {
		return "Multi class segmentation";
	}
	@Override
	public String generatePythonString(String sourcePath,Object model) {
		return "image_datasets."+getPythonName()+"("+this.getDataSetArgs(sourcePath).stream().collect(Collectors.joining(","))+")";
	}

	protected String getPythonName() {
		return "MultiClassSegmentationDataSet";
	}

	protected  ArrayList<String> getDataSetArgs(String sourcePath) {
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add(getImageDirs());
		arrayList.add('"'+sourcePath+'"');
		arrayList.add('"'+getImageIdColumn()+'"');
		arrayList.add('"'+getRLEColumn()+'"');
		arrayList.add('"'+this.clazzColumn.caption()+'"');
		if (this.isRelativeRLE) {
			arrayList.add("isRel=True");
		}
		if (!this.widthFirst) {
			arrayList.add("rMask=False");
		}
		return arrayList;
	}
	protected static ITabularDataSet filter(String clazz, ITabularDataSet base2,String clazzColumn) {
		ITabularDataSet filter = base2.filter(clazzColumn, x->{
			ArrayList<String> splitByClass = MultiClassClassificationItem.splitByClass(x.toString(),null);
			return splitByClass.contains(clazz)?true:false;
		});
		return filter;
	}

	@Override
	public IBinaryClassificationDataSet forClass(String clazz) {
		Map<String, Object> settings = this.getSettings();
		return new BinarySegmentationDataSet(filter(clazz,this.tabularBase,clazzColumn.id()), settings, representer);
	}

	@Override
	public List<ITabularItem> represents(IItem i) {
		MultiClassSegmentationItem it=(MultiClassSegmentationItem) i;
		return it.items;
	}

	@Override
	public void setLabels(LabelsSet labelsSet) {
		this.labels=labelsSet;
	}

	public LabelsSet labels() {
		return this.labels;
	}

	public void setClasses(List<Object> asList) {
		this.classes=asList;
	}

	public List<Object> getClasses() {
		return classes;
	}

	@Override
	public ArrayList<LinkedHashSet<String>> classGroups() {
		return new ArrayList<>(this.classes.stream().map(x->new LinkedHashSet<String>(Arrays.asList(new String[] {x.toString()}))).collect(Collectors.toList()));
	}
}