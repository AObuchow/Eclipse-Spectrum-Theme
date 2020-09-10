package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
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
	private Color prevBackgroundColor;
	private Color prevGlobalFilterColor;
	private IEventBroker eventBroker;
	private ColorManager colorManager;


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
		prevGlobalFilterColor = new ColorHSL(0f, 0f, 0f).getColor();
	}

	@Override
	protected Control createContents(Composite parent) {

		preferenceControl = new SpectrumPreferencesControl(parent, SWT.NONE);
		preferenceControl.getAllColorsButton().setSelection(true);
		preferenceControl.getAllColorsButton().addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> refresh()));
		preferenceControl.getAccentColorButton().addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> refresh()));
		preferenceControl.getBaseColorButton().addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> refresh()));
		preferenceControl.getBackgroundColorButton().addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> refresh()));

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
			if (control.getAllColorsButton().getSelection()) {
				ColorHSL newBackgroundColor = new ColorHSL(prevBackgroundColor).modifyProperty(ColorHSL.HSL_PROPERTY.HUE, ColorHSL.BOUND_BEHAVIOR.CYCLE, selectionValue);
				ColorHSL newBaseColor = new ColorHSL(prevBaseColor).modifyProperty(ColorHSL.HSL_PROPERTY.HUE, ColorHSL.BOUND_BEHAVIOR.CYCLE, selectionValue);
				ColorHSL newAccentColor = new ColorHSL(prevAccentColor).modifyProperty(ColorHSL.HSL_PROPERTY.HUE, ColorHSL.BOUND_BEHAVIOR.CYCLE, selectionValue);
				colorManager.setBackgroundColor(newBackgroundColor.getColor());
				colorManager.setBaseColor(newBaseColor.getColor());
				colorManager.setAccentColor(newAccentColor.getColor());
			}
			
			if (control.getAccentColorButton().getSelection()) {
				ColorHSL newAccentColor = new ColorHSL(colorManager.getAccentColor()).setHue(selectionValue);
				colorManager.setAccentColor(newAccentColor.getColor());
			}
			
			if (control.getBaseColorButton().getSelection()) {
				ColorHSL newBaseColor = new ColorHSL(colorManager.getBaseColor()).setHue(selectionValue);
				colorManager.setBaseColor(newBaseColor.getColor());
			}
			
			if (control.getBackgroundColorButton().getSelection()) {
				ColorHSL newBackgroundColor = new ColorHSL(colorManager.getBackgroundColor()).setHue(selectionValue);
				colorManager.setBackgroundColor(newBackgroundColor.getColor());
			}
			
			colorManager.saveColors();	
		});

		final Scale saturationScale = control.getSaturationScale();
		saturationScale.addListener(SWT.Selection, event -> {
			float selectionValue = ((float) saturationScale.getSelection()) / 100;
			
			if (control.getAllColorsButton().getSelection()) {
				ColorHSL newBackgroundColor = new ColorHSL(prevBackgroundColor).modifyProperty(ColorHSL.HSL_PROPERTY.SATURATION, ColorHSL.BOUND_BEHAVIOR.LIMIT, selectionValue);
				ColorHSL newBaseColor = new ColorHSL(prevBaseColor).modifyProperty(ColorHSL.HSL_PROPERTY.SATURATION, ColorHSL.BOUND_BEHAVIOR.LIMIT, selectionValue);
				ColorHSL newAccentColor = new ColorHSL(prevAccentColor).modifyProperty(ColorHSL.HSL_PROPERTY.SATURATION, ColorHSL.BOUND_BEHAVIOR.LIMIT, selectionValue);
				colorManager.setBackgroundColor(newBackgroundColor.getColor());
				colorManager.setBaseColor(newBaseColor.getColor());
				colorManager.setAccentColor(newAccentColor.getColor());
			}
			
			if (control.getAccentColorButton().getSelection()) {
			ColorHSL newAccentColor = new ColorHSL(colorManager.getAccentColor()).setSaturation(selectionValue);
			colorManager.setAccentColor(newAccentColor.getColor());
			}
			
			if (control.getBaseColorButton().getSelection()) {
			ColorHSL newBaseColor = new ColorHSL(colorManager.getBaseColor()).setSaturation(selectionValue);
			colorManager.setBaseColor(newBaseColor.getColor());
			}
			
			if (control.getBackgroundColorButton().getSelection()) {
			ColorHSL newBackgroundColor = new ColorHSL(colorManager.getBackgroundColor()).setSaturation(selectionValue);
			colorManager.setBackgroundColor(newBackgroundColor.getColor());
			}
			
			colorManager.saveColors();
		});

		final Scale brightnessScale = control.getBrightnessScale();
		brightnessScale.addListener(SWT.Selection, event -> {
			float selectionValue = ((float) brightnessScale.getSelection()) / 100;
			
			if (control.getAllColorsButton().getSelection()) {
				ColorHSL newBackgroundColor = new ColorHSL(prevBackgroundColor).modifyProperty(ColorHSL.HSL_PROPERTY.LUMINANCE, ColorHSL.BOUND_BEHAVIOR.LIMIT, selectionValue);
				ColorHSL newBaseColor = new ColorHSL(prevBaseColor).modifyProperty(ColorHSL.HSL_PROPERTY.LUMINANCE, ColorHSL.BOUND_BEHAVIOR.LIMIT, selectionValue);
				ColorHSL newAccentColor = new ColorHSL(prevAccentColor).modifyProperty(ColorHSL.HSL_PROPERTY.LUMINANCE, ColorHSL.BOUND_BEHAVIOR.LIMIT, selectionValue);
				colorManager.setBackgroundColor(newBackgroundColor.getColor());
				colorManager.setBaseColor(newBaseColor.getColor());
				colorManager.setAccentColor(newAccentColor.getColor());
			}
			
			if (control.getAccentColorButton().getSelection()) {
			ColorHSL newAccentColor = new ColorHSL(colorManager.getAccentColor()).setLuminance(selectionValue);
			colorManager.setAccentColor(newAccentColor.getColor());
			}
			
			if (control.getBaseColorButton().getSelection()) {
			ColorHSL newBaseColor = new ColorHSL(colorManager.getBaseColor()).setLuminance(selectionValue);
			colorManager.setBaseColor(newBaseColor.getColor());
			}
			
			if (control.getBackgroundColorButton().getSelection()) {
			ColorHSL newBackgroundColor = new ColorHSL(colorManager.getBackgroundColor()).setLuminance(selectionValue);
			colorManager.setBackgroundColor(newBackgroundColor.getColor());
			}
			
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
		prevGlobalFilterColor = new ColorHSL(0f, 0f, 0f).getColor();
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
		Color selectedColor = getSelectedColor();
				
		preferenceControl.getHueScale().setSelection((int) new ColorHSL(selectedColor).getHue());
		preferenceControl.getSaturationScale()
				.setSelection((int) (new ColorHSL(selectedColor).getSaturation() * 100));
		preferenceControl.getBrightnessScale()
				.setSelection((int) (new ColorHSL(selectedColor).getLuminance() * 100));
	}

	private Color getSelectedColor() {
		Color selectedColor;
		if (preferenceControl.getBackgroundColorButton().getSelection()) {
			selectedColor = colorManager.getBackgroundColor();
		} else if (preferenceControl.getBaseColorButton().getSelection()) {
			selectedColor = colorManager.getBaseColor();
		}
		else if (preferenceControl.getAccentColorButton().getSelection()) {
			selectedColor = colorManager.getAccentColor();
		} else {			
			selectedColor = prevGlobalFilterColor;
		}
		return selectedColor;
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
