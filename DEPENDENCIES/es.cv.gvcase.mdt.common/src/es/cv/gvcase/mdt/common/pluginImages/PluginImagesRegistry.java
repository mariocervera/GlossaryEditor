/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 ******************************************************************************/
package es.cv.gvcase.mdt.common.pluginImages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

/**
 * This class allows to search extensions for plugins that provides folders with
 * images
 * 
 * @author mgil
 */
public class PluginImagesRegistry {

	/**
	 * The extension point identifier
	 */
	private final String extensionPointID = "es.cv.gvcase.mdt.common.pluginWithImages";

	private List<PluginImages> pluginImagesList;

	/**
	 * Gets the instance for this registry
	 * 
	 * @return
	 */
	public static PluginImagesRegistry getInstance() {
		return new PluginImagesRegistry();
	}

	/**
	 * Get all the plugins with images
	 * 
	 * @param input
	 */
	public List<PluginImages> parsePluginImages() {
		pluginImagesList = new ArrayList<PluginImages>();
		parsePluginsWithImages();
		return pluginImagesList;
	}

	private void parsePluginsWithImages() {
		ExtensionPointParser extensionPointParser = new ExtensionPointParser(
				extensionPointID, new Class[] { PluginImages.class });

		for (Object o : extensionPointParser.parseExtensionPoint()) {
			if (o instanceof PluginImages) {
				PluginImages pluginImage = (PluginImages) Platform
						.getAdapterManager().getAdapter(o, PluginImages.class);
				pluginImagesList.add(pluginImage);
			}
		}
	}

}
