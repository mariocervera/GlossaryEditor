/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop)
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

import org.eclipse.emf.ecore.EObject;

public interface IModelInitializer {

	/**
	 * Initializes the given eObject
	 * 
	 * @param eObject
	 * @return
	 */
	EObject init(EObject eObject);
}
