package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.themes.WorkbenchThemeManager;
import org.eclipse.ui.internal.util.PrefUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class PluginStartup implements IStartup {

	@Override
	public void earlyStartup() {
		IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_RESTYLED, themeRegistryRestyledHandler);
		eventBroker.subscribe(WorkbenchThemeManager.Events.THEME_REGISTRY_MODIFIED, themeRegistryRestyledHandler);
		Activator.getDefault().getColorManager().updateColors();

	}

	private EventHandler themeRegistryRestyledHandler = event -> {
		// TODO: Update git uncommitted changes font to use project explorer font?
		Activator.getDefault().getColorManager().updateColors();
	};

}
