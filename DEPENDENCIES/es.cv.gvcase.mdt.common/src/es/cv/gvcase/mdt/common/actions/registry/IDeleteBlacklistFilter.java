/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop)
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions.registry;

/**
 * This class is used in the Delete model/diagram blacklist registry, to do
 * specific checks to the selected elements to decide if the action will be
 * enabled or not
 * 
 * @author mgil
 */
public interface IDeleteBlacklistFilter {
	/**
	 * Check if the given selection is affected by the delete action. If true is
	 * returned, delete action should be disabled for given selection; if false
	 * is returned, it will be available
	 */
	boolean isAffected(Object object);
}
