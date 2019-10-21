package com.onpositive.musket.data.images;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.actions.BasicDataSetActions;
import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IPythonStringGenerator;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.table.ICSVOVerlay;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ImageRepresenter;

public abstract class AbstractImageDataSet<T extends IImageItem> implements IImageDataSet,Cloneable,IPythonStringGenerator,ICSVOVerlay{

	protected int width;
	protected int height;
	protected ITabularDataSet base;
	protected IColumn imageColumn;
	protected ImageRepresenter representer;
	public ImageRepresenter getRepresenter() {
		return representer;
	}
	protected Map<String,Object>parameters=new LinkedHashMap<String, Object>();
	
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String IMAGE_COLUMN = "IMAGE_COLUMN";
	protected String name = "";
	protected boolean isMultiResolution;
	
	public AbstractImageDataSet(ITabularDataSet base, IColumn imageColumn,int width, int height, ImageRepresenter rep) {
		super();
		this.width = width;
		this.height = height;
		this.base = base;
		this.imageColumn = imageColumn;
		this.representer=rep;
		getSettings().put(AbstractRLEImageDataSet.CLAZZ, this.getClass().getName());
		getSettings().put(IMAGE_COLUMN, imageColumn.id());
		getSettings().put(WIDTH, width);
		getSettings().put(HEIGHT, height);
		
		HashSet<String>strs=new HashSet<>();
		for (int i=0;i<40;i++) {
			String value = imageColumn.getValueAsString(base.get(i));
			BufferedImage bufferedImage = rep.get(value);
			String str=bufferedImage.getWidth()+","+bufferedImage.getHeight();
			strs.add(str);
		}
		if (strs.size()>1) {
			this.isMultiResolution=true;
		}
	}
	protected ArrayList<T> items;
	protected HashMap<String, IImageItem> itemsMap;
	public AbstractImageDataSet(ITabularDataSet base,Map<String,Object>settings,ImageRepresenter rep) {
		this.base=base;
		this.representer=rep;
		this.imageColumn=base.getColumn((String) settings.get(IMAGE_COLUMN));
		
		this.width=Integer.parseInt(settings.get(WIDTH).toString());
		this.height=Integer.parseInt(settings.get(HEIGHT).toString());
		this.parameters=settings;
	}
	public IImageItem getItem(String id) {
		if (itemsMap == null) {
			itemsMap = new HashMap<String, IImageItem>();
			this.items().forEach(v -> {
				itemsMap.put(v.id(), (IImageItem) v);
			});
		}
		return itemsMap.get(id);
	}
	
	public void setSettings(IVisualizerProto proto, Map<String, Object> parameters) {
		this.parameters.putAll(parameters);
	}
	@Override
	public List<DescriptionEntry> description() {
		ArrayList<DescriptionEntry>entries=new ArrayList<>();
		this.fillDescription(entries);
		return entries;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IDataSet subDataSet(String string, List<? extends IItem> arrayList) {
		try {
			AbstractImageDataSet rs = (AbstractImageDataSet) this.clone();
			rs.items = (ArrayList) arrayList;
			rs.name = string;
			return rs;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected ArrayList<AbstractRLEImageDataSet<?>> overlays = new ArrayList<AbstractRLEImageDataSet<?>>();

	public void addOverlay(AbstractRLEImageDataSet<?> overlay) {
		overlays.add(overlay);
	}
	@Override
	public IDataSetDelta compare(IDataSet d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void fillDescription(ArrayList<DescriptionEntry> entries) {
		entries.add(new DescriptionEntry("Kind",this.getKind()));
		entries.add(new DescriptionEntry("Size",this.size()));
	}
	protected abstract String getKind();
	
	public Map<String,Object>getSettings(){
		return parameters;
	}
	@Override
	public IImageItem item(int num) {
		if (this.items==null) {
			this.items();
		}
		return this.items.get(num);
	}
	@Override
	public int length() {
		return items.size();
	}
	@Override
	public List<ConversionAction> conversions() {
		return BasicDataSetActions.getActions(this);
	}
	protected String getImageIdColumn() {
		return this.imageColumn.caption();
	}
	
	protected String getTrainCsv() {
		return null;
	}

	protected String getImageDirs() {
		return this.representer.getImageDirsString();
	}
	
	protected void drawOverlays(String item, BufferedImage image) {
		overlays.forEach(v -> {
			IImageItem item2 = v.getItem(item);
			item2.drawOverlay(image, 0x770000FF);
		});
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public ITabularDataSet original() {
		return base;
	}
	@Override
	public Object modelObject() {
		return null;
	}
	
	@Override
	public String getImportString() {
		return "from musket_core import image_datasets,datasets";
	}
}