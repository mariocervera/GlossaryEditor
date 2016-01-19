/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.utils;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.emf.ui.common.MOSKittUICommonPlugin;

/**
 * This is a utility class to manage Images
 * 
 * @author <a href="mailto:mgil@prodevelop.es">Marc Gil Sendra</a>
 */
public class ImageUtils {

	/**
	 * Image for search
	 */
	public static String IMG_SEARCH = "search.gif";

	/**
	 * Image for refresh
	 */
	public static String IMG_REFRESH = "refresh.gif";

	/**
	 * This method return the Image specified with the given name. You can use
	 * the static string placed in this class to get the available Images.
	 */
	public static Image getSharedImage(String name) {
		Image image = MOSKittUICommonPlugin.getDefault().getImageRegistry()
				.get(name);

		if (image == null) {
			try {
				String path = FileLocator.toFileURL(
						Platform.getBundle(MOSKittUICommonPlugin.PLUGIN_ID)
								.getResource("/icons/")).getPath();
				path += name;
				image = new Image(PlatformUI.getWorkbench().getDisplay(), path);
				MOSKittUICommonPlugin.getDefault().getImageRegistry().put(name,
						image);
			} catch (IOException e) {
				return null;
			}
		}

		return image;
	}

	/**
	 * This method return the ImageDescriptor of the specified Image with the
	 * given name. You can use the static string placed in this class to get the
	 * available Images.
	 */
	public static ImageDescriptor getSharedImageDescriptor(String name) {
		Image image = getSharedImage(name);

		if (image != null) {
			return ImageDescriptor.createFromImage(image);
		} else {
			return null;
		}
	}
}
