package com.onpositive.musket.data.images;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/maskModel.dlf")
public class MaskModel {

	@Caption("Mask size is same as an image size")
	boolean sameAsImage;
	
	
	@Caption("Mask width")
	int width=512;
	
	@Caption("Mask height")
	int height=512;
}
