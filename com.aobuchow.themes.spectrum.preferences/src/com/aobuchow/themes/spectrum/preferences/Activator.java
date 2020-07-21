package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aobuchow.themes.spectrum.preferences"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private ColorManager colorManager;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		colorManager = new ColorManager();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		colorManager.dispose();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * @return the colorManager
	 */
	public ColorManager getColorManager() {
		return colorManager;
	}

}
