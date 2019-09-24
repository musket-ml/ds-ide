package com.onpositive.dataset.visualization.internal;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class Utils {

	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			/*
			 * DirectColorModel colorModel =
			 * (DirectColorModel)bufferedImage.getColorModel(); PaletteData palette = new
			 * PaletteData( colorModel.getRedMask(), colorModel.getGreenMask(),
			 * colorModel.getBlueMask()); ImageData data = new
			 * ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
			 * colorModel.getPixelSize(), palette); WritableRaster raster =
			 * bufferedImage.getRaster(); int[] pixelArray = new int[3]; for (int y = 0; y <
			 * data.height; y++) { for (int x = 0; x < data.width; x++) { raster.getPixel(x,
			 * y, pixelArray); int pixel = palette.getPixel(new RGB(pixelArray[0],
			 * pixelArray[1], pixelArray[2])); data.setPixel(x, y, pixel); } }
			 */
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(),
					colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof ComponentColorModel) {
			ComponentColorModel colorModel = (ComponentColorModel) bufferedImage.getColorModel();
			// ASSUMES: 3 BYTE BGR IMAGE TYPE
			boolean isGrayScale=false;
			PaletteData palette = new PaletteData(0x0000FF, 0x00FF00, 0xFF0000);
			if (bufferedImage.getColorModel().getColorSpace().getType()==ColorSpace.TYPE_GRAY) {
				isGrayScale=true;
				RGB[]arrs=new RGB[256];
				for (int i=0;i<256;i++) {
					arrs[i]=new RGB(i,i,i);
				}
				palette=new PaletteData(arrs);
			}
			
			
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			// This is valid because we are using a 3-byte Data model with no transparent
			// pixels
			data.transparentPixel = -1;
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[4];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel=-1;
					if (isGrayScale) {
						pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[0], pixelArray[0]));
					}
					else pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		return null;
	}
}
