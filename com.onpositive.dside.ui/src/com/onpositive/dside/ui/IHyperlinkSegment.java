package com.onpositive.dside.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public interface IHyperlinkSegment extends IFocusSelectable {
	String getHref();
	String getText();
	void paintFocus(GC gc, Color bg, Color fg, boolean selected, Rectangle repaintRegion);
	boolean contains(int x, int y);
	boolean intersects(Rectangle rect);
}
