package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

import com.onpositive.musket.data.core.AbstractItem;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class BinarySegmentationItem extends AbstractItem<AbstractRLEImageDataSet> implements ISegmentationItem,IBinarySegmentationItem{
	
	protected ITabularItem item;
	private IMask rleMask;
	
	
	public BinarySegmentationItem(AbstractRLEImageDataSet<?> binarySegmentationDataSet, ITabularItem v) {
		super(binarySegmentationDataSet);
		this.item=v;
	}
	
	public BinarySegmentationItem(AbstractRLEImageDataSet<?> binarySegmentationDataSet, RLEMask t) {
		super(binarySegmentationDataSet);
		this.item=null;
		this.rleMask=t;
	}

	
	@Override
	public Image getImage() {
		BufferedImage bufferedImage = owner.representer.get(id());
		BufferedImage image=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);
		
		Object object = owner.getSettings().get(BinaryInstanceSegmentationDataSet.MASK_ALPHA);
		
		Object color = owner.getSettings().get(BinaryInstanceSegmentationDataSet.MASK_COLOR);
		
		int acolor=AbstractRLEImageDataSet.parse(object.toString(),color.toString());
		
		getMask().drawOn(image,acolor);
		owner.drawOverlays(this.id(),image);
		return image;
	}


	

	@Override
	public String id() {
		return owner.imageColumn.getValueAsString(item);
	}
	

	@Override
	public Collection<IMask> getMasks() {
		return Collections.singleton(getMask());
	}

	@Override
	public IMask getMask() {
		if (this.rleMask==null) {
			rleMask = owner.createMask(owner.rleColumn.getValueAsString(item), owner.height, owner.width,this);
		}
		return this.rleMask;
	}

	@Override
	public boolean isPositive() {
		String valueAsString = owner.rleColumn.getValueAsString(item);
		if (valueAsString.isEmpty()||valueAsString.trim().equals("-1")) {
			return false;
		}
		return true;
	}
	@Override
	public void drawOverlay(Image image, int color) {
		getMask().drawOn(image,color);
	}

	@Override
	public Point getImageDimensions() {
		return owner.representer.getDimensions(id());
	}
}
