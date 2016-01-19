/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

/**
 * Class to read the 'es.cv.gvcase.mdt.common.belongToDiagramPolicy' extension
 * point. Represents the behavior policy that is applied to an EditPart to the
 * elements to show.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class EditPartPolicy {

	/**
	 * Fully qualified class name of the affected edit part.
	 */
	public String editPartClass;

	/**
	 * 'whitelist' or ' blacklist' behavior.
	 */
	public String policy;

}
