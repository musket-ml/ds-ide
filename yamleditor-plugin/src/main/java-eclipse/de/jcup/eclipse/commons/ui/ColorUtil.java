package de.jcup.eclipse.commons.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorUtil {

	public static String convertToHexColor(Color color) {
		return color.toString();
	}
	public static String convertToHexColor(RGB color) {
		return color.toString();
	}

}
