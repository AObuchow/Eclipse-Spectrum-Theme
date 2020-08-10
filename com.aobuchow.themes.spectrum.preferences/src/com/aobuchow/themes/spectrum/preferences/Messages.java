package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aobuchow.themes.spectrum.preferences.messages"; //$NON-NLS-1$
	public static String PreferencesPage_LabelHue;
	public static String PreferencesPage_LabelLuminance;
	public static String PreferencesPage_LabelSaturation;
	public static String SpectrumPreferencePage_BugReportLink;
	public static String SpectrumPreferencePage_CommunityGroup;
	public static String SpectrumPreferencePage_CustomizationGroup;
	public static String SpectrumPreferencePage_RepositoryLink;
	public static String SpectrumPreferencePage_GithubURL_Issues;
	public static String SpectrumPreferencePage_GithubURL;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
