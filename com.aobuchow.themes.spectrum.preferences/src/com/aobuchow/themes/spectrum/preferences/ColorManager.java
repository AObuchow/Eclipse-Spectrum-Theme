package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import com.aobuchow.themes.spectrum.preferences.ColorHSL.BOUND_BEHAVIOR;
import com.aobuchow.themes.spectrum.preferences.ColorHSL.HSL_PROPERTY;

public class ColorManager {
	private static final String BACKGROUND_COLOR_ID = "com.aobuchow.themes.spectrum.BACKGROUND_COLOR";
	private static final String BASE_COLOR_ID = "com.aobuchow.themes.spectrum.BASE_COLOR";
	private static final String ACCENT_COLOR_ID = "com.aobuchow.themes.spectrum.ACCENT_COLOR";
	private ColorRegistry colorRegistry;
	private Color accentColor;
	private Color baseColor;
	private Color backgroundColor;
	private IThemeEngine engine;
	private final String THEME_ID = "spectrum.dark.theme.id";
	private Display display;

	public ColorManager() {
		MApplication application = PlatformUI.getWorkbench().getService(MApplication.class);
		IEclipseContext context = application.getContext();
		engine = context.get(IThemeEngine.class);
		display = PlatformUI.getWorkbench().getDisplay();
		updateColors();
	}

	public void updateColors() {
		this.colorRegistry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		accentColor = colorRegistry.get(ACCENT_COLOR_ID);
		baseColor = colorRegistry.get(BASE_COLOR_ID);
		backgroundColor = colorRegistry.get(BACKGROUND_COLOR_ID);
		if (engine.getActiveTheme().getId().equals(THEME_ID)) {
			updateGitColors();
			savePreferences();
		}
	}

	private void savePreferences() {
		try {
			InstanceScope.INSTANCE.getNode(PlatformUI.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getCurrentColorSchemeCSS() {
		String accentColorHex = ColorUtils.colorToHex(accentColor);
		String baseColorHex = ColorUtils.colorToHex(baseColor);
		String backgroundColorHex = ColorUtils.colorToHex(backgroundColor);
		String currentColorScheme = "ColorScheme {\n" + "    --background-color: " + backgroundColorHex + ";\n"
				+ "    --base-color: " + baseColorHex + ";\n" + "    --accent-color: " + accentColorHex + ";\n" + "}";
		return currentColorScheme;
	}

	public void setStyledTextColoring(StyledText colorScheme) {
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		Color black = display.getSystemColor(SWT.COLOR_BLACK);
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

	public void dispose() {
		accentColor.dispose();
		baseColor.dispose();
		backgroundColor.dispose();
	}

	private void updateGitColors() {
		ColorHSL uncommittedChangeBackground = new ColorHSL(backgroundColor).modifyProperty(HSL_PROPERTY.LUMINANCE,
				BOUND_BEHAVIOR.REVERSE, 0.1f);
		uncommittedChangeBackground = uncommittedChangeBackground.modifyProperty(HSL_PROPERTY.SATURATION,
				BOUND_BEHAVIOR.LIMIT, 0.1f);
		uncommittedChangeBackground = uncommittedChangeBackground.modifyProperty(HSL_PROPERTY.HUE, BOUND_BEHAVIOR.CYCLE,
				5f);
		Color uncommittedChangeForeground = ColorUtils.useReadableForegroundColor(
				uncommittedChangeBackground.getColor(), display.getSystemColor(SWT.COLOR_WHITE),
				display.getSystemColor(SWT.COLOR_BLACK));

		setColorPreference("org.eclipse.egit.ui.UncommittedChangeBackgroundColor",
				uncommittedChangeBackground.getColor());
		setColorPreference("org.eclipse.egit.ui.UncommittedChangeForegroundColor", uncommittedChangeForeground);

		uncommittedChangeBackground.dispose();
		uncommittedChangeForeground.dispose();
	}

	private void setColorPreference(String preferenceKey, Color color) {
		// We can't use PlatformUI.getPreferenceStore() as it won't affect preferences
		// for plugins such as EGit
		PlatformUI.getWorkbench().getPreferenceStore().setValue(preferenceKey,
				String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue()));
	}

}
