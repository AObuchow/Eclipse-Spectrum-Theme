package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.themes.WorkbenchThemeManager;
import org.osgi.service.event.EventHandler;

import com.aobuchow.themes.spectrum.preferences.ColorHSL.BOUND_BEHAVIOR;
import com.aobuchow.themes.spectrum.preferences.ColorHSL.HSL_PROPERTY;

public class PreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	private Color prevAccentColor;
	private Color prevBaseColor;
	private IEventBroker eventBroker;
	private ColorManager colorManager;
	private Color prevBackgroundColor;

	private SpectrumPreferencesControl preferenceControl;

	@Override
	public void init(IWorkbench workbench) {
		colorManager = Activator.getDefault().getColorManager();
		eventBroker = workbench.getService(IEventBroker.class);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_RESTYLED, themeRegistryRestyledHandler);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_MODIFIED, themeRegistryRestyledHandler);
		setPreferenceStore(PlatformUI.getPreferenceStore());

		prevBackgroundColor = colorManager.getBackgroundColor();
		prevBaseColor = colorManager.getBaseColor();
		prevAccentColor = colorManager.getAccentColor();
	}

	@Override
	protected Control createContents(Composite parent) {

		preferenceControl = new SpectrumPreferencesControl(parent, SWT.NONE);

		addHSLCustomization(preferenceControl);

		preferenceControl.getRepoLink().addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e -> openURL("repo", Messages.SpectrumPreferencePage_GithubURL)));

		preferenceControl.getIssuesLink().addSelectionListener(SelectionListener
				.widgetSelectedAdapter(e -> openURL("issues", Messages.SpectrumPreferencePage_GithubURL_Issues)));

		String currentColorScheme = Activator.getDefault().getColorManager().getCurrentColorSchemeCSS();
		final StyledText cssText = preferenceControl.getCssText();
		cssText.setText(currentColorScheme);
		Activator.getDefault().getColorManager().setStyledTextColoring(cssText);

		refresh();

		return preferenceControl;
	}

	private void addHSLCustomization(SpectrumPreferencesControl control) {
		final Scale hueScale = control.getHueScale();
		hueScale.addListener(SWT.Selection, event -> {
			int selectionValue = hueScale.getSelection();
			ColorHSL newBackgroundColor = new ColorHSL(colorManager.getBackgroundColor()).setHue(selectionValue);
			ColorHSL newBaseColor = new ColorHSL(prevBaseColor).setHue(selectionValue)
					.modifyProperty(HSL_PROPERTY.SATURATION, BOUND_BEHAVIOR.LIMIT, -0.1f);
			ColorHSL newAccentColor = new ColorHSL(colorManager.getAccentColor()).setHue(selectionValue);
			colorManager.setBackgroundColor(newBackgroundColor.getColor());
			colorManager.setBaseColor(newBaseColor.getColor());
			colorManager.setAccentColor(newAccentColor.getColor());
			colorManager.saveColors();
		});

		final Scale saturationScale = control.getSaturationScale();
		saturationScale.addListener(SWT.Selection, event -> {
			float selectionValue = ((float) saturationScale.getSelection()) / 100;
			ColorHSL newBackgroundColor = new ColorHSL(colorManager.getBackgroundColor()).setSaturation(selectionValue);
			colorManager.setBackgroundColor(newBackgroundColor.getColor());
			colorManager.saveColors();
		});

		final Scale brightnessScale = control.getBrightnessScale();
		brightnessScale.addListener(SWT.Selection, event -> {
			float selectionValue = ((float) brightnessScale.getSelection()) / 100;
			ColorHSL newBackgroundColor = new ColorHSL(colorManager.getBackgroundColor()).setLuminance(selectionValue);
			colorManager.setBackgroundColor(newBackgroundColor.getColor());
			colorManager.saveColors();
		});
	}

	@Override
	public boolean performOk() {
		prevBackgroundColor = colorManager.getBackgroundColor();
		prevBaseColor = colorManager.getBaseColor();
		prevAccentColor = colorManager.getAccentColor();
		refresh();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		colorManager.resetColors();
		refresh();
		super.performDefaults();
	}

	@Override
	public boolean performCancel() {
		colorManager.setBackgroundColor(prevBackgroundColor);
		colorManager.setBaseColor(prevBaseColor);
		colorManager.setAccentColor(prevAccentColor);
		colorManager.saveColors();
		prevBackgroundColor = colorManager.getBackgroundColor();
		prevBaseColor = colorManager.getBaseColor();
		prevAccentColor = colorManager.getAccentColor();
		refresh();
		return super.performCancel();
	}

	private EventHandler themeRegistryRestyledHandler = event -> {
		// Update the relevant UI when the theme's colors are modified
		if (!getControl().isDisposed()) {
			String currentColorScheme = Activator.getDefault().getColorManager().getCurrentColorSchemeCSS();
			final StyledText cssText = preferenceControl.getCssText();
			cssText.setText(currentColorScheme);
			Activator.getDefault().getColorManager().setStyledTextColoring(cssText);
		}
	};

	private void refresh() {
		refreshScales();
		refreshCssText();
	}

	private void refreshScales() {
		preferenceControl.getHueScale().setSelection((int) new ColorHSL(colorManager.getBackgroundColor()).getHue());
		preferenceControl.getSaturationScale()
				.setSelection((int) (new ColorHSL(colorManager.getBackgroundColor()).getSaturation() * 100));
		preferenceControl.getBrightnessScale()
				.setSelection((int) (new ColorHSL(colorManager.getBackgroundColor()).getLuminance() * 100));
	}

	private void refreshCssText() {
		String currentColorScheme = Activator.getDefault().getColorManager().getCurrentColorSchemeCSS();
		final StyledText cssText = preferenceControl.getCssText();
		if (!cssText.getText().equals(currentColorScheme)) {
			cssText.setText(currentColorScheme);
		}
	}

	private void openURL(String category, String url) {
		BrowserUtils.openUrl(category, url);
	}

}
