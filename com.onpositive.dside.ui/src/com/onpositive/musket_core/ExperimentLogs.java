package com.onpositive.musket_core;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

import com.opencsv.CSVReader;

public class ExperimentLogs {
	
	String name;

	String[] headers;
	ArrayList<Double[]> data = new ArrayList<>();

	public ExperimentLogs(String name,String path) {
		try {
			this.name=name;
			new CSVReader(new FileReader(path)).forEach(l -> {
				if (headers == null) {
					headers = l;
				} else {
					Double[] res = new Double[l.length];
					for (int i = 0; i < res.length; i++) {
						res[i] = Double.parseDouble(l[i]);
					}
					this.data.add(res);
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> metrics() {
		ArrayList<String> rs = new ArrayList<>();
		if (headers==null) {
			return rs;
		}
		for (String s : this.headers) {
			if (s.startsWith("val_")) {
				rs.add(s.substring(4));
			}
		}
		return rs;
	}

	private DefaultXYDataset createDataset(String metric) {

		
		
		DefaultXYDataset dataset = new DefaultXYDataset();

		
		add(metric, dataset, "Train");
		add("val_"+metric, dataset, "Validation");

		return dataset;
	}

	private void add(String metric, DefaultXYDataset dataset, String series1) {
		int index = -1;
		for (int i = 0; i < this.headers.length; i++) {
			if (this.headers[i].equals( metric)) {
				index = i;
			}
		}
		if (index != -1) {
			double[][] m = new double[2][this.data.size()];
			for (int i = 0; i < this.data.size(); i++) {
				m[0][i] = i;
				m[1][i] = this.data.get(i)[index];
			}
			dataset.addSeries(series1, m);
		}
	}

	/**
	 * snippet 156: convert between SWT Image and AWT BufferedImage.
	 * <p>
	 * For a list of all SWT example snippets see
	 * http://www.eclipse.org/swt/snippets/
	 */
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
			PaletteData palette = new PaletteData(0x0000FF, 0x00FF00, 0xFF0000);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			// This is valid because we are using a 3-byte Data model with no transparent
			// pixels
			data.transparentPixel = -1;
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		return null;
	}

	public ImageData toImage(String metric, int width, int height) {
		JFreeChart chart = ChartFactory.createXYLineChart("", // Chart title
				"Epoch", // X-Axis Label
				metric, // Y-Axis Label
				this.createDataset(metric));

		BufferedImage createBufferedImage = chart.createBufferedImage(width, height);
		return convertToSWT(createBufferedImage);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
