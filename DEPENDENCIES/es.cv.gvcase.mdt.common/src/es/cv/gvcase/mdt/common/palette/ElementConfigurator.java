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
 * Interface to allow the configuration of the elements created by a template tool defined via extension points. <br>
 * Used in the TemnplateTool extension point. <br>
 * If none is provided a default one is used.
 * 
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 *
 * @see BaseElementConfigurator
 * @see TemplateToolRegistry
 */
public interface ElementConfigurator {

	EObject configureElement(EObject createdEObject, Element element);
	
}
