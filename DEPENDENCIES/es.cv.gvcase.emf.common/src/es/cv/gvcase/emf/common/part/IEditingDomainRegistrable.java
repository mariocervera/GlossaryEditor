/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin Cubero (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.emf.common.part;

/**
 * Used in the {@link EditingDomainRegistry}.
 * 
 * @author <a href="mailto:gmerin@prodevelop.es">Gabriel Merin Cubero</a>.
 * 
 */
public interface IEditingDomainRegistrable {

	/**
	 * Return the URI of the editing domain resource
	 * 
	 * @return The String representing the resource URI
	 */
	public String getEditingDomainResourceURI();

	/**
	 * Return the ID of the editing domain
	 * 
	 * @return
	 */
	public String getEditingDomainID();

}
