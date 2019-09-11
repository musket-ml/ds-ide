package com.onpositive.musket.data.images;

import java.awt.Color;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;

public class SegmentationVisualisationSettings extends ImageDataSetVisualisationSettings{

	@Caption("Show masks")
	protected boolean drawMasks;
	
	@Caption("Color of the masks")
	protected Color maskColor=new Color(0xFFFF0000, true);

}
