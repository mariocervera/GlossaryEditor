/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.palette;

public class Group {
	/**
	 * Palette container identifier.
	 */
	public String groupID;
	/**
	 * Label, applied to new gropus.
	 */
	public String label;
	/**
	 * If true, this group will be removed
	 */
	public Boolean remove;

	public Group() {
	}
}
