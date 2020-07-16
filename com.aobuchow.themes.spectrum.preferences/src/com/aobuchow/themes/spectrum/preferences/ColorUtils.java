package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.swt.graphics.Color;

public class ColorUtils {

	private static final String RGB_TO_HEX = "#%02X%02X%02X";

	public static String colorToHex(Color color) {
		return String.format(RGB_TO_HEX, color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public ColorHSL ColorToHSL(Color color) {
		return null;
	}
	
	public static int maxRGB(Color color) {
		return Math.max(color.getRed(), Math.max(color.getGreen(), color.getBlue()));
	}
	
	public static int minRGB(Color color) {
		return Math.min(color.getRed(), Math.min(color.getGreen(), color.getBlue()));
	}

	// TODO: Reimplement using ColorHSL.getPerceivableLuminance()
	@Deprecated
	public static Color useReadableForegroundColor(Color backgroundColor, Color white, Color black) {
		// TODO: This is probably wrong...
		double luminance = 0.2126 * backgroundColor.getRed() + 0.7152 * backgroundColor.getGreen()
				+ 0.0722 * backgroundColor.getBlue();
		return luminance > 128 ? black : white;
	}

}
