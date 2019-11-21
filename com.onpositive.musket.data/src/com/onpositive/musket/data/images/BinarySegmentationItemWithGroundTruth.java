package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.table.ITabularItem;

public class BinarySegmentationItemWithGroundTruth extends BinarySegmentationItem implements IBinaryClassificationItemWithPrediction{

	private ITabularItem prediction;
	private IMask prleMask;

	public BinarySegmentationItemWithGroundTruth(AbstractRLEImageDataSet<?> binarySegmentationDataSet, ITabularItem v,ITabularItem prediction) {
		super(binarySegmentationDataSet, v);
		this.prediction=prediction;
	}

	public IItem getPrediction() {
		return new BinarySegmentationItem(owner, prediction);
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
		
		object = owner.getSettings().get(BinarySegmentationDataSetWithGroundTruth.PREDICTION_ALPHA);
		
		color = owner.getSettings().get(BinarySegmentationDataSetWithGroundTruth.PREDICTION_COLOR);
		
		acolor=AbstractRLEImageDataSet.parse(object.toString(),color.toString());
		
		getPredictionMask().drawOn(image,acolor);
		owner.drawOverlays(this.id(),image);
		return image;
		
	}
	
	public IMask getPredictionMask() {
		if (this.prleMask==null) {
			prleMask = owner.createMask(owner.rleColumn.getValueAsString(prediction), owner.height, owner.width,this);
		}
		return this.prleMask;
	}

	public boolean isPredictionPositive() {
		String valueAsString = owner.rleColumn.getValueAsString(prediction);
		if (valueAsString.isEmpty()||valueAsString.trim().equals("-1")) {
			return false;
		}
		return true;
	}
}
