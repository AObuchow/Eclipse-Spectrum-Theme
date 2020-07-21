package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.swt.graphics.Color;

public interface ColorUtils {

	static final String RGB_TO_HEX = "#%02X%02X%02X";

	public static String colorToHex(Color color) {
		return String.format(RGB_TO_HEX, color.getRed(), color.getGreen(), color.getBlue());
	}

	public static int maxRGB(Color color) {
		return Math.max(color.getRed(), Math.max(color.getGreen(), color.getBlue()));
	}
	
	public static int minRGB(Color color) {
		return Math.min(color.getRed(), Math.min(color.getGreen(), color.getBlue()));
	}

	public static Color useReadableForegroundColor(Color backgroundColor, Color white, Color black) {
		return new ColorHSL(backgroundColor).getPerceivedLuminance() >= 127.5f ? black : white;
	}

}
