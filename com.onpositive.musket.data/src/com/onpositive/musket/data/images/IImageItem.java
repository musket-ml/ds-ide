package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.Point;

import com.onpositive.musket.data.core.IItem;


public interface IImageItem extends IItem{

	Image getImage();

	
	void drawOverlay(Image image,int color);


	Point getImageDimensions();
}