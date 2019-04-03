package com.onpositive.dside.ui;

import java.util.Hashtable;

import org.eclipse.swt.graphics.Rectangle;

public interface IFocusSelectable {
	boolean isFocusSelectable(Hashtable<String, Object> resourceTable);
	boolean setFocus(Hashtable<String, Object> resourceTable, boolean direction);
	Rectangle getBounds();
}
