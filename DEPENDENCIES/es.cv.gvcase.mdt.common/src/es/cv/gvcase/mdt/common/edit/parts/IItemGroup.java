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
package es.cv.gvcase.mdt.common.edit.parts;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;

/**
 * An ItemGroup represents a collection of elements connected by a parent
 * elements. <br>
 * In an item group each selectable element can have a target {@link View} to be
 * shown and a target {@link EObject} to show in that view.
 * 
 * @author <a href="mailto:fjcano@prodevleop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface IItemGroup {

	/**
	 * Retrieves all the possible selectable items.
	 * 
	 * @return
	 */
	Collection<EObject> getSelectableItems();

	/**
	 * Retrieves the currently selected item.
	 * 
	 * @return
	 */
	EObject getSelected();

	/**
	 * Sets the curretly selected item.
	 * 
	 * @param eObject
	 */
	void setSelected(EObject eObject);

	/**
	 * Returns the target view for the given element.
	 * 
	 * @param eObject
	 * @return
	 */
	View getTargetView();

	/**
	 * Returns the {@link EObject} for the target view.
	 * 
	 * @return
	 */
	EObject getEObjectForView();

}
