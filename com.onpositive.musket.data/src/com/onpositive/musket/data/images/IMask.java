package com.onpositive.musket.data.images;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.BitSet;

public interface IMask {

	default Image getImage() {
		return getImage(0xFFFF0000);
	}
	
	Image getImage(int color);
	
	Rectangle getBounds();
	
	public BitSet getBooleanMask();
	
	public default IMask union(IMask m) {
		BitSet union = this.getBooleanMask();
		union.or(m.getBooleanMask());
		if (m instanceof RelativeRLEMask) {
			return new RelativeRLEMask(union,m.getWidth(),m.getHeight());
		}
		return new RLEMask(union,m.getWidth(),m.getHeight());
	}
	
	int getHeight();

	int getWidth();

	public default double iou(IMask m1) {
		BitSet union = this.getBooleanMask();
		union.or(m1.getBooleanMask());
		
		BitSet intersection=this.getBooleanMask();
		intersection.and(m1.getBooleanMask());
		
		return intersection.cardinality()/(union.cardinality()+0.01);
	}
	
	default void drawOn(Image image,int color) {
		drawOn(image, color, 1.0f);		
	}

	default void drawOn(Image image,int color, float alpha) {
		Graphics2D g=(Graphics2D) image.getGraphics();
		Composite composite=AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g.setComposite(composite);
		//AffineTransform affineTransform = new AffineTransform(); 
		//rotate the image by 45 degrees 
		//affineTransform.rotate(Math.toRadians(-90),1024,1024); 
		//g2d.drawImage(image, m_affineTransform, null);
		Image image2 = this.getImage(color);
		g.drawImage(image2, 0,0, image.getWidth(null),image.getHeight(null), null);
	}
	default void drawOn(Image image) {
		Graphics2D g=(Graphics2D) image.getGraphics();
		//Composite composite=AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
		//g.setComposite(composite);		
		Image image2 = this.getImage();
		g.drawImage(image2,0, 0, image.getWidth(null),image.getHeight(null), null);
	}

	String rle();
	
	String rawRle();
	
	String addPatchRle();
	
	String removePatchRle();

	default boolean isEmpty() {
		return getBooleanMask().cardinality()==0;
	}

	void update(Image image);
	
	void remove(Image image);
	
	void add(Image image);
	
	public void updateFrom(BitSet bitset);
	
	public String clazz();
}