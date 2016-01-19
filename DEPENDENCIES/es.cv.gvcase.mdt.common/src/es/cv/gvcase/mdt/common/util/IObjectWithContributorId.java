/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin Cubero (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

/**
 * Meant to implemented by the objects used in the ExtensionPointParser.
 * 
 * @author <a href="mailto:gmerin@prodevelop.es">Gabriel Merín Cubero</a>
 * 
 */
public interface IObjectWithContributorId {

	/**
	 * Return the Plugin's ID that made the contribution of this object
	 * 
	 * @return The Plugin's ID String
	 */
	public String getContributorId();

	/**
	 * Set the Plugin's ID that made the contribution of this object
	 */
	public void setContributorId(String contributorId);
	
}
