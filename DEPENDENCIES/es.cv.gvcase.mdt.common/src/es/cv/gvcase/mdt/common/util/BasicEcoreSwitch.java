/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

import org.eclipse.emf.ecore.EObject;

/**
 * Basic implementation of an {@link EcoreSwitch}, with info available for the
 * Switch.
 * 
 * @param <T>
 *            the type returned by the doSwitch() method
 * @param <I>
 *            the type of the info
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public abstract class BasicEcoreSwitch<T, I> implements EcoreSwitch<T, I> {

	/** The extra info that will be available to perform the switch. */
	I info = null;

	/**
	 * Basic constructor.
	 */
	public BasicEcoreSwitch() {
		;
	}

	/**
	 * Constructor with additional information.
	 * 
	 * @param info
	 *            the info
	 */
	public BasicEcoreSwitch(I info) {
		this.info = info;
	}

	/**
	 * Gets the extra information, if any.
	 * 
	 * @return the info
	 */
	public I getInfo() {
		return info;
	}

	/**
	 * Set the extra information.
	 */
	public void setInfo(I info) {
		this.info = info;
	}

	/**
	 * The switch method. To be implemented by subclasses.
	 * 
	 * @param object
	 *            the object
	 * 
	 * @return the T
	 */
	public abstract T doSwitch(EObject object);

}
