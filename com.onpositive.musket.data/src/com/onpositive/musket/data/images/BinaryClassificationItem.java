package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.onpositive.musket.data.core.AbstractItem;
import com.onpositive.musket.data.table.ITabularItem;

public class BinaryClassificationItem extends AbstractItem<BinaryClassificationDataSet> implements IBinaryClasificationItem,IImageItem{
	
	
	protected ITabularItem item;
	
	
	public BinaryClassificationItem(BinaryClassificationDataSet binarySegmentationDataSet, ITabularItem v) {
		super(binarySegmentationDataSet);
		this.item=v;
	}
	
	protected boolean isPositiveValue(Object value) {
		ArrayList<Object> classes = owner.classes;
		
		if (value.equals(owner.classes.get(0))) {
			return false;
		}
		if (value.equals(owner.classes.get(1))) {
			return true;
		}
		String valueAsString=value.toString();
		if (valueAsString.isEmpty()||valueAsString.trim().equals("0")||valueAsString.trim().equalsIgnoreCase("false")) {
			return false;
		}
		
		return true;
	}
	@Override
	public Image getImage() {
		BufferedImage bufferedImage = owner.representer.get(id());
		BufferedImage image=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);		
		owner.drawOverlays(this.id(),image);
		return image;
	}

	@Override
	public String id() {
		return owner.imageColumn.getValueAsString(item);
	}

	
	@Override
	public boolean isPositive() {
		return isPositiveValue(owner.clazzColumn.getValue(item));
		
	}
	@Override
	public void drawOverlay(Image image, int color) {
		
	}

	@Override
	public Point getImageDimensions() {
		return owner.representer.getDimensions(id());
	}
}
