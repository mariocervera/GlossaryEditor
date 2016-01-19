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
package es.cv.gvcase.mdt.common.edit.policies;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Provides a {@link ViewAndFeatureResolver} with resolution of the parent
 * {@link EObject}. To be used when the parent is not directly the containing
 * element in the metamodel structure.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface ViewFeatureParentResolver extends ViewAndFeatureResolver {

	/**
	 * Provides the parent where an {@link EObject} should be stored according
	 * to the given {@link EClass}.
	 * 
	 * @param eClass
	 * @return
	 */
	EObject getParentForEClass(EClass eClass);
}
