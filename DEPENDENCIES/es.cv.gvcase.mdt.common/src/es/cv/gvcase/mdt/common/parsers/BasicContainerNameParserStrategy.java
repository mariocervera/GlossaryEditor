/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.parsers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.parsers.ContainerNameParserWrapper.ContainerNameParserStrategy;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * A basic {@link ContainerNameParserStrategy}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class BasicContainerNameParserStrategy implements
		ContainerNameParserStrategy {

	/**
	 * The {@link View} where the affected EditPart is enclosed.
	 */
	private View view = null;

	public BasicContainerNameParserStrategy(View view) {
		this.view = view;
	}

	/**
	 * Gets the container name if it must be shown or null otherwise.
	 */
	public String getContainerName(IAdaptable element) {
		Object object = element.getAdapter(EObject.class);
		if (object instanceof EObject) {
			if (mustShowContainer((EObject) object)) {
				return MDTUtil.getObjectName(((EObject) object)
						.eContainer());
			}
		}
		return null;
	}

	/**
	 * True if the container's name must be shown.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	protected boolean mustShowContainer(EObject eObject) {
		if (eObject == null) {
			return false;
		}
		EObject container = eObject.eContainer();
		if (container == null) {
			return false;
		}
		EObject parentViewContainer = getContainerViewEObject();
		return container.equals(parentViewContainer) == false;
	}

	/**
	 * Gets the container view e object.
	 * 
	 * @return the container view e object
	 */
	private EObject getContainerViewEObject() {
		EObject element = view.getElement();
		View parent = (View) view.eContainer();

		while (parent != null && parent.getElement() != null
				&& parent.getElement().equals(element)) {
			parent = (View) parent.eContainer();
		}

		return parent != null ? parent.getElement() : null;
	}

}
