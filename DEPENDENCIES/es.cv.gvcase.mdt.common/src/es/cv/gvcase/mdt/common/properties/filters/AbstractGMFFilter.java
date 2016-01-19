/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.properties.filters;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IFilter;

/**
 * An abstract IFilter that takes an array of Class objects to check which
 * elements are selectable.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractGMFFilter implements IFilter {

	private Class[] classes = null;

	/**
	 * The given array of Class objects will be used to check the selectable
	 * elements.
	 * 
	 * @param classes
	 */
	public AbstractGMFFilter(Class[] classes) {
		this.classes = classes;
		if (this.classes == null) {
			this.classes = new Class[0];
		}
	}

	protected Class[] getClasses() {
		return classes;
	}

	protected EObject getEObject(Object selected) {
		if (selected instanceof EditPart) {
			selected = ((EditPart) selected).getModel();
		}
		if (selected instanceof View) {
			selected = ((View) selected).getElement();
		}
		if (selected instanceof EObject) {
			return (EObject) selected;
		}
		return null;
	}

	public boolean select(Object toTest) {
		EObject eObject = getEObject(toTest);
		if (eObject == null) {
			return false;
		}
		for (Class clazz : getClasses()) {
			if (clazz.isInstance(eObject)) {
				return checkAfterClass(eObject, clazz);
			}
		}
		return false;
	}
	
	protected boolean checkAfterClass(Object toTest, Class clazz) {
		return true;
	}

}
