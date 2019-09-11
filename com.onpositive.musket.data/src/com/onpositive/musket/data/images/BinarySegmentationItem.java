package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class BinarySegmentationItem implements ISegmentationItem,IBinarySegmentationItem{
	
	protected AbstractRLEImageDataSet<?> base;
	protected ITabularItem item;
	private IMask rleMask;
	
	
	public BinarySegmentationItem(AbstractRLEImageDataSet<?> binarySegmentationDataSet, ITabularItem v) {
		this.base=binarySegmentationDataSet;
		this.item=v;
	}
	
	public BinarySegmentationItem(AbstractRLEImageDataSet<?> binarySegmentationDataSet, RLEMask t) {
		this.base=binarySegmentationDataSet;
		this.item=null;
		this.rleMask=t;
	}

	
	@Override
	public Image getImage() {
		BufferedImage bufferedImage = base.representer.get(id());
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
		return base.imageColumn.getValueAsString(item);
	}

	
	@Override
	public IDataSet getDataSet() {
		return base;
	}

	@Override
	public Collection<IMask> getMasks() {
		return Collections.singleton(getMask());
	}

	@Override
	public IMask getMask() {
		if (this.rleMask==null) {
			rleMask = base.createMask(base.rleColumn.getValueAsString(item), base.height, base.width);
		}
		return this.rleMask;
	}

	@Override
	public boolean isPositive() {
		String valueAsString = base.rleColumn.getValueAsString(item);
		if (valueAsString.isEmpty()||valueAsString.trim().equals("-1")) {
			return false;
		}
		return true;
	}
	@Override
	public void drawOverlay(Image image, int color) {
		getMask().drawOn(image,color);
	}
}
