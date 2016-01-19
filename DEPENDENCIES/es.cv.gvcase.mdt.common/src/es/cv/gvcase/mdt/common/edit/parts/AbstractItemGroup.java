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

/**
 * An initial implementation of an {@link IItemGroup}. <br>
 * Clients may override and subclass.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractItemGroup implements IItemGroup {

	protected EObject parentEObject = null;

	protected EObject selectedEObject = null;

	public AbstractItemGroup(EObject eObject) {
		this.parentEObject = eObject;
	}

	public Collection<EObject> getSelectableItems() {
		return parentEObject.eContents();
	}

	public EObject getSelected() {
		return selectedEObject;
	}

	public void setSelected(EObject eObject) {
		this.selectedEObject = eObject;
	}

}
