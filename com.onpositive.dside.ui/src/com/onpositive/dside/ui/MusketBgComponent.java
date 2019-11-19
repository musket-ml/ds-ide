package com.onpositive.dside.ui;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Component providing background showing simple Dense neural network
 * Not used currently
 *  
 * @author 32kda
 */
public class MusketBgComponent extends Canvas implements PaintListener {

	private int xStep;
	private int yStep;
	private int radius;
	private ColorRegistry colorRegistry = new ColorRegistry(Display.getDefault());
	private int lineWidth = 4;

	public MusketBgComponent(Composite parent, int xStep, int yStep, int radius, RGB lowColor, RGB hiColor) {
		super(parent, SWT.NONE);
		this.xStep = xStep;
		this.yStep = yStep;
		this.radius = radius;
		addPaintListener(this);
		for (int i = 0; i <= 10; i++) {
			double ratio = i / 10.0;
			RGB curColor = new RGB((int)Math.round(lowColor.red * ratio + hiColor.red * (1 - ratio)),
					(int)Math.round(lowColor.green * ratio + hiColor.green * (1 - ratio)),
					(int)Math.round(lowColor.blue * ratio + hiColor.blue * (1 - ratio)));
			colorRegistry.put(i + "", curColor);
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		Point size = getSize();
		int layerNodeCnt = (size.y - 4 * radius) / yStep + 1;
		int layerCnt = (size.x - 4 * radius) / xStep + 1;
		int startX = radius * 2;
		int startY = radius * 2;
		GC gc = e.gc;
		gc.fillRectangle(getBounds());
		if (layerNodeCnt < 2 || layerCnt < 2) {
			return;
		}
		int[] curValues = new int[layerNodeCnt];
		int[] prevValues = null;
		gc.setLineAttributes(new LineAttributes(lineWidth));
		for (int i = 0; i < layerCnt; i++) {			
			int xCenter = startX + (i - 1) * xStep;
			for (int j = 0; j < curValues.length; j++) {
				curValues[j] = (int) Math.round(Math.random() * 10);
				if (prevValues != null) {
					for (int k = 0; k < prevValues.length; k++) {
						gc.setForeground(colorRegistry.get("" + prevValues[k]));
						gc.drawLine(xCenter, startY + k * yStep, startX + i * xStep, startY + j * yStep);
					}
				}
			}
			if (prevValues != null) {
				for (int k = 0; k < prevValues.length; k++) {
					int yCenter = startY + k * yStep;
					int value = prevValues[k];
					drawNode(gc, xCenter, yCenter, value);
				}
			}
			prevValues = curValues;
			curValues = new int[layerNodeCnt];
		}
		
		int xCenter = startX + (layerCnt - 1) * xStep;
		
		for (int k = 0; k < prevValues.length; k++) {
			int yCenter = startY + k * yStep;
			int value = prevValues[k];
			drawNode(gc, xCenter, yCenter, value);
		}
		
	}

	private void drawNode(GC gc, int xCenter, int yCenter, int value) {
		gc.setBackground(colorRegistry.get("" + value));
		gc.fillOval(xCenter - radius, yCenter - radius, radius * 2, radius * 2);
		gc.setBackground(getBackground());
		int sz = (int) Math.round(radius * 1.2);
		gc.fillOval(xCenter - sz / 2, yCenter - sz / 2, sz, sz);
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}
	

}
