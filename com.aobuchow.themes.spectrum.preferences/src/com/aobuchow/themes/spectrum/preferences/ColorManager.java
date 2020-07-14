package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
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
	
	public static void setStyledTextColoring(StyledText colorScheme, ColorRegistry colorRegistry) {
		Color accentColor = colorRegistry.get("com.aobuchow.themes.spectrum.ACCENT_COLOR"); //$NON-NLS-1$
		Color baseColor = colorRegistry.get("com.aobuchow.themes.spectrum.BASE_COLOR"); //$NON-NLS-1$
		Color backgroundColor = colorRegistry.get("com.aobuchow.themes.spectrum.BACKGROUND_COLOR"); //$NON-NLS-1$
		Color white = new Color(colorScheme.getDisplay(), 255, 255, 255);
		Color black = new Color(colorScheme.getDisplay(), 0, 0, 0);
		String text = colorScheme.getText();

		StyleRange bgStyle = new StyleRange();
		bgStyle.start = text.indexOf("--background-color:") + "--background-color:".length() + 1;
		bgStyle.length = 7;
		bgStyle.fontStyle = SWT.BOLD;
		bgStyle.background = backgroundColor;
		bgStyle.foreground = ColorUtils.useReadableForegroundColor(backgroundColor, white, black);
		colorScheme.setStyleRange(bgStyle);

		StyleRange baseStyle = new StyleRange();
		baseStyle.start = text.indexOf("--base-color:") + "--base-color:".length() + 1;
		baseStyle.length = 7;
		baseStyle.fontStyle = SWT.BOLD;
		baseStyle.background = baseColor;
		baseStyle.foreground = ColorUtils.useReadableForegroundColor(baseColor, white, black);
		colorScheme.setStyleRange(baseStyle);

		StyleRange accentStyle = new StyleRange();
		accentStyle.start = text.indexOf("--accent-color:") + "--accent-color:".length() + 1;
		accentStyle.length = 7;
		accentStyle.fontStyle = SWT.BOLD;
		accentStyle.foreground = ColorUtils.useReadableForegroundColor(accentColor, white, black);
		accentStyle.background = accentColor;
		colorScheme.setStyleRange(accentStyle);
	}

}
