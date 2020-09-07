package com.aobuchow.themes.spectrum.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.themes.WorkbenchThemeManager;
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
	private IEventBroker eventBroker;

	public ColorManager() {
		MApplication application = PlatformUI.getWorkbench().getService(MApplication.class);
		IEclipseContext context = application.getContext();
		engine = context.get(IThemeEngine.class);
		display = PlatformUI.getWorkbench().getDisplay();
		eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		updateColors();
	}

	// Should only be called from PluginStartup theme registry listener, clients
	// should use saveColors() instead
	public void updateColors() {
		this.colorRegistry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		accentColor = colorRegistry.get(ACCENT_COLOR_ID);
		baseColor = colorRegistry.get(BASE_COLOR_ID);
		backgroundColor = colorRegistry.get(BACKGROUND_COLOR_ID);
		if (engine.getActiveTheme().getId().equals(THEME_ID)) {
			updateGitColors();
			updateThemeFontColors();
			savePreferences();
		}
	}

	public void saveColors() {
		setColorPreference(BACKGROUND_COLOR_ID, backgroundColor);
		colorRegistry.put(BACKGROUND_COLOR_ID, backgroundColor.getRGB());
		setColorPreference(BASE_COLOR_ID, baseColor);
		colorRegistry.put(BASE_COLOR_ID, baseColor.getRGB());
		setColorPreference(ACCENT_COLOR_ID, accentColor);
		colorRegistry.put(ACCENT_COLOR_ID, accentColor.getRGB());
		savePreferences();
		updateColors();
		eventBroker.send(WorkbenchThemeManager.Events.THEME_REGISTRY_MODIFIED, null);
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
		display.asyncExec(() -> {
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
		});
	}

	public void dispose() {
		accentColor.dispose();
		baseColor.dispose();
		backgroundColor.dispose();
	}

	private void updateGitColors() {
		display.asyncExec(() -> {
			ColorHSL uncommittedChangeBackground = new ColorHSL(backgroundColor).modifyProperty(HSL_PROPERTY.LUMINANCE,
					BOUND_BEHAVIOR.REVERSE, 0.1f);
			uncommittedChangeBackground = uncommittedChangeBackground.modifyProperty(HSL_PROPERTY.SATURATION,
					BOUND_BEHAVIOR.LIMIT, 0.1f);
			uncommittedChangeBackground = uncommittedChangeBackground.modifyProperty(HSL_PROPERTY.HUE,
					BOUND_BEHAVIOR.CYCLE, 5f);
			Color uncommittedChangeForeground = ColorUtils.useReadableForegroundColor(
					uncommittedChangeBackground.getColor(), display.getSystemColor(SWT.COLOR_WHITE),
					display.getSystemColor(SWT.COLOR_BLACK));
			setColorPreference("org.eclipse.egit.ui.UncommittedChangeForegroundColor", uncommittedChangeForeground);
			setColorPreference("org.eclipse.egit.ui.UncommittedChangeBackgroundColor",
					uncommittedChangeBackground.getColor());
		});
	}
	
	private void updateThemeFontColors() {
		display.asyncExec(() -> {
			Color primaryFontColor = ColorUtils.useReadableForegroundColor(
					backgroundColor, display.getSystemColor(SWT.COLOR_WHITE),
					display.getSystemColor(SWT.COLOR_BLACK));
			Color activeFontColor = ColorUtils.useReadableForegroundColor(
					accentColor, display.getSystemColor(SWT.COLOR_WHITE),
					display.getSystemColor(SWT.COLOR_BLACK));
			Color inactiveFontColor = ColorUtils.useReadableForegroundColor(
					baseColor, display.getSystemColor(SWT.COLOR_WHITE),
					display.getSystemColor(SWT.COLOR_BLACK));
			setColorPreference("com.aobuchow.themes.spectrum.PRIMARY_FONT_COLOR", primaryFontColor);
			setColorPreference("com.aobuchow.themes.spectrum.ACTIVE_FONT_COLOR", activeFontColor);
			setColorPreference("com.aobuchow.themes.spectrum.INACTIVE_FONT_COLOR", inactiveFontColor);
		});
	}

	private void setColorPreference(String preferenceKey, Color color) {
		// We can't use PlatformUI.getPreferenceStore() as it won't affect preferences
		// for plugins such as EGit
		PlatformUI.getWorkbench().getPreferenceStore().setValue(preferenceKey,
				String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue()));
	}

	public Color getAccentColor() {
		return accentColor;
	}

	public void setAccentColor(Color accentColor) {
		this.accentColor = accentColor;
	}

	public Color getBaseColor() {
		return baseColor;
	}

	public void setBaseColor(Color baseColor) {
		this.baseColor = baseColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public List<Color> getThemeColors() {
		List<Color> colors = new ArrayList<>();
		colors.add(accentColor);
		colors.add(baseColor);
		colors.add(backgroundColor);
		return colors;
	}

	public void resetColors() {
		this.setBackgroundColor(new Color(display, 23, 23, 27));
		this.setBaseColor(new Color(display, 47, 47, 47));
		this.setAccentColor(new Color(display, 215, 0, 0));
		this.saveColors();
	}

}
