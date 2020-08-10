package com.aobuchow.themes.spectrum.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public final class BrowserUtils {

	public static void openUrl(String category, String url) {
		try {
			IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport()
					.createBrowser("spectrum.browser." + category);
			browser.openURL(new URL(url));
		} catch (PartInitException | MalformedURLException e) {
			final Status status = new Status(IStatus.ERROR, BrowserUtils.class, "Cannot open URL '" + url + "'", e);
			Activator.getDefault().getLog().log(status);
		}
	}

}
