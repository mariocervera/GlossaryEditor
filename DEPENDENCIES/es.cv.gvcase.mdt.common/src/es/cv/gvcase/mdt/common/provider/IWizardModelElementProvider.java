/***************************************************************************
* Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
*
******************************************************************************/

package es.cv.gvcase.mdt.common.provider;

import org.eclipse.emf.ecore.EObject;

/**
 * Simple interface to provide a model element.
 */
public interface IWizardModelElementProvider {

	/**
	 * Gets the model element.
	 * 
	 * @return the model element
	 */
	EObject getModelElement();
	
}
