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
package es.cv.gvcase.mdt.common.model;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

/**
 * A Factory to create new basic types described in an abstract way.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public interface ITypesFactory {

	/**
	 * Creates a basic type from its abstract description.
	 * 
	 * @param type
	 * @return
	 */
	EObject createType(Type type);
	
	/**
	 * Creates a basic type from its abstract description and stores it in the model.
	 * 
	 * @param type
	 * @return
	 */
	EObject createTypeInModel(Type type, EObject model);
	
	/**
	 * Created a group of basic types from its abstract description.
	 * 
	 * @param group
	 * @return
	 */
	Collection<EObject> createTypeGroup(TypesGroup group);
	
	/**
	 * Created a group of basic types from its abstract description and stores them in the model.
	 * 
	 * @param group
	 * @return
	 */
	Collection<EObject> createTypeGroupInModel(TypesGroup group, EObject model);
	
}
