package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.swt.graphics.Color;

public class ColorUtils {
	
	private static final String rgbToHex = "#%02X%02X%02X";
	
	public static String colorToHex(Color color) {
			return String.format(rgbToHex, color.getRed(), color.getGreen(), color.getBlue());			
	}

}
