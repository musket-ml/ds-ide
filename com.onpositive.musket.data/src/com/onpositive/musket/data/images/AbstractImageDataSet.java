package com.onpositive.musket.data.images;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.images.actions.BasicImageDataSetActions;
import com.onpositive.musket.data.images.actions.BasicImageDataSetActions.ConversionAction;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ImageRepresenter;

public abstract class AbstractImageDataSet<T extends IImageItem> implements IImageDataSet,Cloneable{

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
		return this.items.get(num);
	}
	@Override
	public int length() {
		return items.size();
	}
	@Override
	public List<ConversionAction> conversions() {
		return BasicImageDataSetActions.getActions(this);
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
}