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
package com.onpositive.dside.ui.views;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

final class GalleryTooltip
extends DefaultToolTip {
    private final Gallery gallery;

    GalleryTooltip(Control control, Gallery gallery) {
        super(control);
        this.gallery = gallery;
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

