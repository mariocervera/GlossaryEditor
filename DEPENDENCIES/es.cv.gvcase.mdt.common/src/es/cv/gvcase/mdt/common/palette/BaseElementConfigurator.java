/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.palette;

import org.eclipse.emf.ecore.EObject;

/**
 * Basic implementation of {@link ElementConfigurator}. <br>
 * This basic configuration does no modifications to the created elements.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see ElementConfigurator
 * @see TemplateToolRegistry
 */
public class BaseElementConfigurator implements ElementConfigurator {

	protected static BaseElementConfigurator INSTANCE = null;

	public static BaseElementConfigurator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BaseElementConfigurator();
		}
		return INSTANCE;
	}

	public EObject configureElement(EObject createdEObject, Element element) {
		// no configuration takes place in the configureElement.
		return createdEObject;
	}

}
