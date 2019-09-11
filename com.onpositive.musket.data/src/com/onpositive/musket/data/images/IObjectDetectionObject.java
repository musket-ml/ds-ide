package com.onpositive.musket.data.images;

import java.awt.Rectangle;

public interface IObjectDetectionObject {

	int clazz();

	Rectangle bounds();
	
	int[] attributes();
}
