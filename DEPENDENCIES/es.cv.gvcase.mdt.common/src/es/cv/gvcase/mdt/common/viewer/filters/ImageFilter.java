/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin (Prodevelop) – Initial implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.viewer.filters;


public class ImageFilter extends FileFilter {
	private final static String[] extensions = { "gif", "png", "jpeg", "jpg",
			"gif", "bmp", "ico", "tiff" };

	public ImageFilter() {
		super(extensions);
	}
}