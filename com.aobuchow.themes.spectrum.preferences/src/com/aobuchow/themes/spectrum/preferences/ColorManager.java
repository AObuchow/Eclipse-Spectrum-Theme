package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;

public class ColorManager {

	public static String getCurrentColorSchemeCSS() {
		ColorRegistry colorRegistry = Activator.getDefault().getColorRegistry();
		Color accentColor = colorRegistry.get("com.aobuchow.themes.spectrum.ACCENT_COLOR"); //$NON-NLS-1$
		Color baseColor = colorRegistry.get("com.aobuchow.themes.spectrum.BASE_COLOR"); //$NON-NLS-1$
		Color backgroundColor = colorRegistry.get("com.aobuchow.themes.spectrum.BACKGROUND_COLOR"); //$NON-NLS-1$
		
		String accentColorHex = ColorUtils.colorToHex(accentColor);
		String baseColorHex = ColorUtils.colorToHex(baseColor); 
		String backgroundColorHex =  ColorUtils.colorToHex(backgroundColor);
		String currentColorScheme = "ColorScheme {\n" +  //$NON-NLS-1$
				"    --background-color: " + backgroundColorHex + ";\n" +  //$NON-NLS-1$ //$NON-NLS-2$
				"    --base-color: " + baseColorHex + ";\n" +  //$NON-NLS-1$ //$NON-NLS-2$
				"    --accent-color: " + accentColorHex + ";\n" +  //$NON-NLS-1$ //$NON-NLS-2$
				"}"; //$NON-NLS-1$
		return currentColorScheme;
	}

}
