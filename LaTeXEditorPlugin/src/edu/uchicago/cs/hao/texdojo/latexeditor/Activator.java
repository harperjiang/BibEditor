/*******************************************************************************
 * Copyright (c) 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package edu.uchicago.cs.hao.texdojo.latexeditor;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceChangeMonitor;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.uchicago.cs.hao.texdojo.latexeditor"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private PreferenceChangeMonitor monitor = new PreferenceChangeMonitor();

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// Install Preference Change Monitor
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		prefs.addPreferenceChangeListener(monitor);

		// Initialize Image Resources
		initImages();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// Remove Preference Change Monitor
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		prefs.removePreferenceChangeListener(monitor);

		plugin = null;
		super.stop(context);

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
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	protected void initImages() {
		getImageRegistry().put(ImageResource.ICON_CHAPTER, new Image(Display.getCurrent(),
				Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/chapter_icon.png")));
		getImageRegistry().put(ImageResource.ICON_LIST, new Image(Display.getCurrent(),
				Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/list_icon.png")));
		getImageRegistry().put(ImageResource.ICON_ITEM, new Image(Display.getCurrent(),
				Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/item_icon.png")));
		getImageRegistry().put(ImageResource.ICON_FIGURE, new Image(Display.getCurrent(),
				Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/figure_icon.png")));
		getImageRegistry().put(ImageResource.ICON_TABLE, new Image(Display.getCurrent(),
				Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/table_icon.png")));
		getImageRegistry().put(ImageResource.ICON_ALGO, new Image(Display.getCurrent(),
				Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/algo_icon.png")));
	}

}
