/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.swt.custom.CLabel
 *  org.eclipse.swt.graphics.Color
 *  org.eclipse.swt.graphics.Font
 *  org.eclipse.swt.graphics.Image
 *  org.eclipse.swt.graphics.Point
 *  org.eclipse.swt.graphics.Rectangle
 *  org.eclipse.swt.layout.FillLayout
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Control
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Layout
 */
package com.onpositive.dataset.visualization.internal;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

final class GalleryTooltip extends DefaultToolTip {
	private final Gallery gallery;

	GalleryTooltip(Control control, Gallery gallery) {
		super(control);
		this.gallery = gallery;
	}

	private Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		//image.dispose(); // don't forget about me!
		return scaled;
	}

	protected Composite createToolTipContentArea(Event event, Composite parent) {
        Image image = this.getImage(event);
        Image bgImage = this.getBackgroundImage(event);
        String text = this.getText(event);
        Color fgColor = this.getForegroundColor(event);
        Color bgColor = this.getBackgroundColor(event);
        Font font = this.getFont(event);
        FillLayout layout = (FillLayout)parent.getLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 5;
        parent.setBackground(bgColor);
        
        Rectangle bounds = image.getBounds();
		if (bounds.width>900||bounds.height>900) {
			if (bounds.width>bounds.height) {
				image=resize(image, 900, (int) (bounds.height/(bounds.width/900.0)));	
			}
			else {
				image=resize(image,  (int) (bounds.width/(bounds.height/900.0)),900);	
			}
        }
        CLabel label = new CLabel(parent, this.getStyle(event));
        if (text != null) {
            label.setText(text);
        }
        if (image != null) {
            label.setImage(image);
        }
        if (fgColor != null) {
            label.setForeground(fgColor);
        }
        if (bgColor != null) {
            label.setBackground(bgColor);
        }
        if (bgImage != null) {
            label.setBackgroundImage(image);
        }
        if (font != null) {
            label.setFont(font);
        }
        Point computeSize = label.computeSize(-1, -1);
        parent.setSize(500, 500);
        return label;
    }

	protected Image getImage(Event event) {
		Point point = new Point(event.x, event.y);
		GalleryItem item = this.gallery.getItem(point);
		if (item != null && item.getParent() != null) {
			return item.getImage();
		}
		return super.getImage(event);
	}

	protected boolean shouldCreateToolTip(Event event) {
		boolean shouldCreateToolTip = super.shouldCreateToolTip(event);
		Point point = new Point(event.x, event.y);
		GalleryItem item = this.gallery.getItem(point);
		if (shouldCreateToolTip && item != null) {
			return true;
		}
		return false;
	}

	protected String getText(Event event) {
		Point point = new Point(event.x, event.y);
		GalleryItem item = this.gallery.getItem(point);
		if (item != null) {
			if (item.getParent() != null) {
				return item.getData().toString();
			}
			return item.getText();
		}
		return super.getText(event);
	}
}