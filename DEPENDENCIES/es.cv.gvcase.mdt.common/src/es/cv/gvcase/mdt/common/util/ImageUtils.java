/*******************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin Cubero (Prodevelop) – initial API and
 * implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.util;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import es.cv.gvcase.mdt.common.Activator;

/**
 * @author <a href="mailto:gmerin@prodevelop.es">Gabriel Merin Cubero</a>
 */
public class ImageUtils {

	/**
	 * Returns the {@link Image} represented by an {@link URL} string (ex:
	 * platform:/resource/folder/file.xxx, etc.) checking first if the file
	 * exists. If the URL string is malformed or if the image cannot be loaded,
	 * null is returned.
	 * 
	 * @param URL
	 *            string
	 * @return the Image represented by the URL string if possible. Null
	 *         otherwise.
	 */
	public static Image getImageFromURLString(String url) {
		Image image;
		String imagePath;
		URL imageURL;

		// if url is null or blank, return directly null
		if (url == null || url.trim().length() == 0)
			return null;

		try {
			imageURL = new URL(url);
			imagePath = FileLocator.toFileURL(imageURL).getFile();
			image = new Image(Display.getCurrent(), imagePath);
		} catch (IOException ex) {
			image = null;
		}
		return image;
	}

	/**
	 * Return the {@link Image} contained in the given bundle and with the provided
	 * path.
	 * 
	 * @param bundleId
	 *            The Plugin ID where the image is contained
	 * @param imagePath
	 *            Path within the plugin to find the
	 * @return the SWT Image
	 */
	public static Image getImageFromBundle(String bundleId, String imagePath) {
		ImageDescriptor imgDes = AbstractUIPlugin.imageDescriptorFromPlugin(
				bundleId, imagePath);
		Image image = null;
		try {
			image = imgDes.createImage();
		} catch (Exception ex) {
			Activator.getDefault().logError(
					"Could not load contributed Image. Bundle: " + bundleId
							+ " | ImagePath: " + imagePath, ex);
		}
		if (image != null)
			return image;
		else
			return null;
	}

	/**
	 * Returns the {@link Image} area scaled downwards following the reduced area but
	 * respecting the aspect ratio of the image
	 * 
	 * @param imageArea
	 *            is the rectangle that describes the area of the image
	 * @param reducedArea
	 *            is the rectangle where the image is going to be shown,
	 *            typically with a smaller size than the image area
	 * @return
	 */
	public static Rectangle reduceRespectingAspectRatio(Rectangle imageArea,
			Rectangle reducedArea) {
		// If the reduced area is greater than the image area, return the same
		// image area
		if (reducedArea.width >= imageArea.width
				&& reducedArea.height >= imageArea.height) {
			Rectangle newArea = new Rectangle(imageArea);
			newArea.x = reducedArea.x;
			newArea.y = reducedArea.y;
			return newArea;
		} else {
			double wFactor = (double) reducedArea.width
					/ (double) imageArea.width;
			double hFactor = (double) reducedArea.height
					/ (double) imageArea.height;

			// Get the most restrictive factor
			double factor = wFactor <= hFactor ? wFactor : hFactor;
			Rectangle newArea = new Rectangle(imageArea);
			int newWidth = (int) Math.floor(newArea.width * factor);
			int newHeight = (int) Math.floor(newArea.height * factor);

			newArea.width = newWidth;
			newArea.height = newHeight;
			newArea.x = reducedArea.x;
			newArea.y = reducedArea.y;
			return newArea;
		}
	}
}
