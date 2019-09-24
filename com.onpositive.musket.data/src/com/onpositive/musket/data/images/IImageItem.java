package com.onpositive.musket.data.images;

import java.awt.Image;

import com.onpositive.musket.data.core.IItem;


public interface IImageItem extends IItem{

	Image getImage();

	
	void drawOverlay(Image image,int color);
}