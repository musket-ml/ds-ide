package com.onpositive.dside.ui;

import java.util.ArrayList;

public class Locator implements Cloneable {
	public int indent;
	public int x, y;
	public int width;
	public int leading;
	public int rowHeight;
	public int marginWidth;
	public int marginHeight;
	public int rowCounter;
	public ArrayList<int[]> heights;

	public void newLine() {
		resetCaret();
		y += rowHeight;
		rowHeight = 0;
	}

	public Locator create() {
		try {
			return (Locator)clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
	public void collectHeights() {
		heights.add(new int [] { rowHeight, leading} );
		rowCounter++;
	}
	public int getBaseline(int segmentHeight) {
		return getBaseline(segmentHeight, true);

	}
	public int getMiddle(int segmentHeight, boolean text) {
		if (heights!=null && heights.size()>rowCounter) {
			int [] rdata = heights.get(rowCounter);
			int rheight = rdata[0];
			int rleading = rdata[1];
			if (text)
				return y + rheight/2 - segmentHeight/2 - rleading;
			return y + rheight/2 - segmentHeight/2;
		}
		return y;
	}
	public int getBaseline(int segmentHeight, boolean text) {
		if (heights!=null && heights.size()>rowCounter) {
			int [] rdata = heights.get(rowCounter);
			int rheight = rdata[0];
			int rleading = rdata[1];
			if (text)
				return y + rheight - segmentHeight - rleading;
			return y + rheight - segmentHeight;
		}
		return y;
	}

	public void resetCaret() {
		x = getStartX();
	}
	public int getStartX() {
		return marginWidth + indent;
	}
}
