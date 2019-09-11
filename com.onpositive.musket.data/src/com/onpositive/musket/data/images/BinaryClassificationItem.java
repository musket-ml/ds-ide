package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class BinaryClassificationItem implements IBinaryClasificationItem,IImageItem{
	
	protected BinaryClassificationDataSet base;
	protected ITabularItem item;
	
	
	public BinaryClassificationItem(BinaryClassificationDataSet binarySegmentationDataSet, ITabularItem v) {
		this.base=binarySegmentationDataSet;
		this.item=v;
	}
	
	protected boolean isPositiveValue(Object value) {
		String valueAsString=value.toString();
		if (valueAsString.isEmpty()||valueAsString.trim().equals("0")||valueAsString.trim().equalsIgnoreCase("false")) {
			return false;
		}
		return true;
	}
	@Override
	public Image getImage() {
		BufferedImage bufferedImage = base.representer.get(id());
		BufferedImage image=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);		
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
	public boolean isPositive() {
		return isPositiveValue(base.clazzColumn.getValue(item));
		
	}
	@Override
	public void drawOverlay(Image image, int color) {
		
	}
}