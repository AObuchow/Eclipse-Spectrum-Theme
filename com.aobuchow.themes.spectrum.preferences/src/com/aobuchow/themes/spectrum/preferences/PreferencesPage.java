package com.aobuchow.themes.spectrum.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.themes.WorkbenchThemeManager;
import org.osgi.service.event.EventHandler;

import com.aobuchow.themes.spectrum.preferences.ColorHSL.BOUND_BEHAVIOR;
import com.aobuchow.themes.spectrum.preferences.ColorHSL.HSL_PROPERTY;

public class PreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	private IWebBrowser browser;
	private URL issuesURL;
	private URL repoURL;
	private StyledText colorScheme;
	private Color prevAccentColor;
	private Color prevBaseColor;
	private IEventBroker eventBroker;
	private ColorManager colorManager;
	private Color prevBackgroundColor;
	private Scale hueScale;
	private Scale saturationScale;
	private Scale luminanceScale;

	@Override
	public void init(IWorkbench workbench) {
		colorManager = Activator.getDefault().getColorManager();
		browser = BrowserUtils.newBrowser();
		eventBroker = workbench.getService(IEventBroker.class);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_RESTYLED, themeRegistryRestyledHandler);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_MODIFIED, themeRegistryRestyledHandler);
		setPreferenceStore(PlatformUI.getPreferenceStore());

		prevBackgroundColor = colorManager.getBackgroundColor();
		prevBaseColor = colorManager.getBaseColor();
		prevAccentColor = colorManager.getAccentColor();

		try {
			issuesURL = new URL("https://github.com/AObuchow/Eclipse-Spectrum-Theme/issues"); //$NON-NLS-1$
			repoURL = new URL("https://github.com/AObuchow/Eclipse-Spectrum-Theme"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			Activator.getDefault().getLog().log(new Status(ERROR, getClass(), e.getMessage()));
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		Group customizeGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		customizeGroup.setText(Messages.SpectrumPreferencePage_CustomizationGroup);
		customizeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		customizeGroup.setLayout(layout);

		addHSLCustomization(customizeGroup);

		colorScheme = new StyledText(customizeGroup, SWT.LEAD | SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
		colorScheme.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		String currentColorScheme = Activator.getDefault().getColorManager().getCurrentColorSchemeCSS();
		colorScheme.setText(currentColorScheme);
		Activator.getDefault().getColorManager().setStyledTextColoring(colorScheme);

		Group communityGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		communityGroup.setText(Messages.SpectrumPreferencePage_CommunityGroup);
		communityGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		communityGroup.setLayout(layout);
		BrowserUtils.createLinkURL(communityGroup, Messages.SpectrumPreferencePage_RepositoryLink, repoURL, browser);
		BrowserUtils.createLinkURL(communityGroup, Messages.SpectrumPreferencePage_BugReportLink, issuesURL, browser);

		return composite;
	}

	private void addHSLCustomization(Group customizeGroup) {
		Text hueLabel = new Text(customizeGroup, SWT.BOLD);
		hueLabel.setText(Messages.PreferencesPage_LabelHue);
		hueScale = newScale(customizeGroup, 0, 360,
				(int) new ColorHSL(colorManager.getBackgroundColor()).getHue());
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

		Text saturationLabel = new Text(customizeGroup, SWT.BOLD);
		saturationLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true));
		saturationLabel.setText(Messages.PreferencesPage_LabelSaturation);
		saturationScale = newScale(customizeGroup, 0, 100,
				(int) (new ColorHSL(colorManager.getBackgroundColor()).getSaturation() * 100));
		saturationScale.addListener(SWT.Selection, event -> {
			float selectionValue = ((float) saturationScale.getSelection()) / 100;
			ColorHSL newBackgroundColor = new ColorHSL(colorManager.getBackgroundColor()).setSaturation(selectionValue);
			colorManager.setBackgroundColor(newBackgroundColor.getColor());
			colorManager.saveColors();
		});

		Text luminanceLabel = new Text(customizeGroup, SWT.BOLD);
		luminanceLabel.setText(Messages.PreferencesPage_LabelLuminance);
		luminanceScale = newScale(customizeGroup, 0, 100,
				(int) (new ColorHSL(colorManager.getBackgroundColor()).getLuminance() * 100));
		luminanceScale.addListener(SWT.Selection, event -> {
			float selectionValue = ((float) luminanceScale.getSelection()) / 100;
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
		updateScales();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		colorManager.resetColors();
		updateScales();
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
		updateScales();
		return super.performCancel();
	}

	private EventHandler themeRegistryRestyledHandler = event -> {
		// Update the relevant UI when the theme's colors are modified
		if (!colorScheme.isDisposed()) {
			String currentColorScheme = Activator.getDefault().getColorManager().getCurrentColorSchemeCSS();
			colorScheme.setText(currentColorScheme);
			Activator.getDefault().getColorManager().setStyledTextColoring(colorScheme);
		}
	};

	private void updateScales() {
		hueScale.setSelection((int) new ColorHSL(colorManager.getBackgroundColor()).getHue());
		saturationScale.setSelection((int) (new ColorHSL(colorManager.getBackgroundColor()).getSaturation() * 100));
		luminanceScale.setSelection((int) (new ColorHSL(colorManager.getBackgroundColor()).getLuminance() * 100));
	}

	private static Scale newScale(Composite parent, int min, int max, int selection) {
		Scale scale = new Scale(parent, SWT.BORDER);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Rectangle clientArea = parent.getClientArea();
		scale.setBounds(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
		scale.setMaximum(max);
		scale.setMinimum(min);
		scale.setSelection(selection);
		scale.setPageIncrement(5);
		scale.setIncrement(5);
		return scale;
	}

}
