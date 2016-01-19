/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

import org.eclipse.swt.widgets.Display;

public class GraphicUtils {
	/**
	 * Converts the given millimeters into pixels for the default Display
	 * 
	 * @param millimeters
	 * @return
	 */
	public static int millimetersToPixels(double millimeters) {
		int dpi = Display.getCurrent().getDPI().x;
		double milimeterPerInch = 25.4;

		double pixelPerMM = dpi / milimeterPerInch;

		double pixels = pixelPerMM * millimeters;

		return Math.round(Math.round(pixels));
	}

	/**
	 * Converts the given pixels into millimeters for the default Display
	 * 
	 * @param millimeters
	 * @return
	 */
	public static int pixelToMillimeters(int pixels) {
		int dpi = Display.getCurrent().getDPI().x;
		double millimeterPerInch = 25.4;

		double pixelPerMM = dpi / millimeterPerInch;

		double millimeters = pixels / pixelPerMM;

		return Math.round(Math.round(millimeters));
	}
}
