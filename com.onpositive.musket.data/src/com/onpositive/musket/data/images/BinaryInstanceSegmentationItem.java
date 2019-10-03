package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class BinaryInstanceSegmentationItem implements ISegmentationItem,IBinarySegmentationItem,IInstanceSegmentationItem{
	
	protected BinaryInstanceSegmentationDataSet base;
	protected ArrayList<ITabularItem> items;
	private ArrayList<IMask> rleMasks;
	private String id;
	
	public BinaryInstanceSegmentationItem(String id,BinaryInstanceSegmentationDataSet binarySegmentationDataSet, ArrayList<ITabularItem> items) {
		this.base=binarySegmentationDataSet;
		this.items=items;
		this.id=id;
	}

	@Override
	public Image getImage() {
		BufferedImage bufferedImage = base.representer.get(id);
		BufferedImage image=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);
		
		Object object = base.getSettings().get(BinaryInstanceSegmentationDataSet.MASK_ALPHA);
		
		Object color = base.getSettings().get(BinaryInstanceSegmentationDataSet.MASK_COLOR);
		
		int acolor=AbstractRLEImageDataSet.parse(object.toString(),color.toString());
		
		getMask().drawOn(image,acolor);
		base.drawOverlays(this.id(),image);
		return image;
	}

	

	@Override
	public String id() {
		return id;
	}

	
	
	@Override
	public IDataSet getDataSet() {
		return base;
	}

	@Override
	public ArrayList<IMask> getMasks() {
		if (this.rleMasks==null) {
			ArrayList<IMask>mssk=new ArrayList<IMask>();
			this.items.forEach(v->{
				mssk.add( base.createMask(base.rleColumn.getValueAsString(v), base.height, base.width,this));
			});
			this.rleMasks=mssk;
		}
		return this.rleMasks;
	}

	@Override
	public IMask getMask() {
		IMask r=null;
		for (IMask m:getMasks()) {
			if (r==null) {
				r=m;
			}
			else {
				r=r.union(m);
			}
		}
		if (r==null) {
			return base.createMask("-1", base.height, base.width,this);
		}
		return r;
	}

	@Override
	public boolean isPositive() {
		if (this.items.isEmpty()) {
			return false;
		}
		if (this.getMask().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public List<? extends ISegmentationItem> items() {
		ArrayList<BinarySegmentationItem>result=new ArrayList<BinarySegmentationItem>();
		this.items.forEach(v->{
			BinarySegmentationItem bi=new BinarySegmentationItem(this.base, v);
			if (bi.isPositive()) {
				result.add(bi);
			}
		});
		return result;
	}

	@Override
	public void drawOverlay(Image image, int color) {
		getMask().drawOn(image,color);
	}

	@Override
	public Point getImageDimensions() {
		return base.representer.getDimensions(id());
	}

}