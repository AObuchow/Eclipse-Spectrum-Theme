package com.aobuchow.themes.spectrum.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.themes.WorkbenchThemeManager;
import org.osgi.service.event.EventHandler;

public class PreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	private IWebBrowser browser;
	private URL issuesURL;
	private URL repoURL;
	private StyledText colorScheme;

	@Override
	public void init(IWorkbench workbench) {
		browser = BrowserUtils.newBrowser();
		IEventBroker eventBroker = workbench.getService(IEventBroker.class);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_RESTYLED, themeRegistryRestyledHandler);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_MODIFIED, themeRegistryRestyledHandler);
		setPreferenceStore(PlatformUI.getPreferenceStore());

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

	private EventHandler themeRegistryRestyledHandler = event -> {
		// Update the relevant UI when the theme's colors are modified
		if (!colorScheme.isDisposed()) {
			String currentColorScheme = Activator.getDefault().getColorManager().getCurrentColorSchemeCSS();
			colorScheme.setText(currentColorScheme);
			Activator.getDefault().getColorManager().setStyledTextColoring(colorScheme);
		}
	};

}
