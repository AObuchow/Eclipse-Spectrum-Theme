package com.aobuchow.themes.spectrum.preferences;

import java.net.URL;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public interface BrowserUtils {

	public static IWebBrowser newBrowser() {
		IWebBrowser browser = null;
		try {
			browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("spectrum.browser"); //$NON-NLS-1$
		} catch (PartInitException e) {
			// Internal browser shouldn't cause an exception
			Activator.getDefault().getLog().log(new Status(org.eclipse.jface.dialogs.IMessageProvider.ERROR, BrowserUtils.class, e.getMessage()));
		}

		try {
			browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
		} catch (PartInitException e) {
			Activator.getDefault().getLog().log(new Status(org.eclipse.jface.dialogs.IMessageProvider.INFORMATION, BrowserUtils.class, e.getMessage()));
		}
		
		return browser;
	}
	
	public static Link createLinkURL(Group parent, String text, URL url, IWebBrowser webBrowser) {
		Link link = new Link(parent, SWT.BORDER | SWT.BOLD);
		link.setText(text);
		link.addListener(SWT.Selection, event -> {
			try {
				webBrowser.openURL(url);
			} catch (PartInitException e) {
				Activator.getDefault().getLog().log(new Status(org.eclipse.jface.dialogs.IMessageProvider.ERROR, BrowserUtils.class, e.getMessage()));
			}
		});
		return link;
	}

}
