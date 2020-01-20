package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.ITabularItem;

public class MultiClassSegmentationItemWithPrediction extends MultiClassSegmentationItem implements IBinaryClassificationItemWithPrediction,IMulticlassClassificationItem{

	public MultiClassSegmentationItemWithPrediction(String id, MultiClassSegmentationDataSet binarySegmentationDataSet,
			ArrayList<ITabularItem> items) {
		super(id, binarySegmentationDataSet, items);
	}

	@Override
	public boolean isPredictionPositive() {
		return ((IBinaryClasificationItem)this.getPrediction()).isPositive();
	}

	@Override
	public IItem getPrediction() {
		return ((MultiClassSegmentationDataSetWithGrounTruth)this.owner).getPrediction(this.id());
	}
	
	@Override
	public Image getImage() {
		BufferedImage bufferedImage = owner.representer.get(id());
		BufferedImage image=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);
		//getMask().drawOn(image,0x777777);
		
		Object object = owner.getSettings().get(BinarySegmentationDataSetWithGroundTruth.MASK_ALPHA);
		drawMasks(image,object);
		object = owner.getSettings().get(BinarySegmentationDataSetWithGroundTruth.PREDICTION_ALPHA);
		owner.drawOverlays(this.id(),image);
		MultiClassSegmentationItem it=(MultiClassSegmentationItem) getPrediction();
		it.drawMasks((BufferedImage) image,object);
		return image;
	}

	
}
