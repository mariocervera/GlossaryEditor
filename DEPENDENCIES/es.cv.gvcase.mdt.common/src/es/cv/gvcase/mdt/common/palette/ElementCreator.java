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
 * Interface that allows the custom creation of elements in the TemplateTool
 * extension point. <br>
 * If none is provided in the extension point, a default one is used.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see BaseElementCreator
 * @see TemplateToolRegistry
 * 
 */
public interface ElementCreator {

	EObject createElement(EObject parent, Element element);
	
}
