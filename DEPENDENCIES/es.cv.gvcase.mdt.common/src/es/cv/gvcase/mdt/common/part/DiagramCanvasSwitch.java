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
package es.cv.gvcase.mdt.common.part;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Utility methods to demultiplex {@link EClass}es and {@link EObject}s for different
 * canvas elements.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DiagramCanvasSwitch {

	/**
	 * Gets the {@link Diagram}s {@link EObject} given an {@link EditPolicy}.
	 * 
	 * @param policy the policy
	 * 
	 * @return the canvas element
	 */
	public static EObject getCanvasElement(EditPolicy policy) {
		EditPart editPart = policy != null ? policy.getHost() : null;
		return MDTUtil.resolveSemantic(editPart);
	}

	/**
	 * Gets the element to edit in a mutating canvas {@link Diagram}. By default, it's
	 * the canvas semantic {@link EObject}.
	 * 
	 * @param editPolicy the edit policy
	 * 
	 * @return the element to edit for create command
	 */
	public static EObject getElementToEditForCreateCommand(EditPolicy editPolicy) {
		return getCanvasElement(editPolicy);
	}

	/**
	 * Gets the {@link EClass} to edit. It's the EClass of the canvas {@link EObject}
	 * 
	 * @param editPolicy the edit policy
	 * 
	 * @return the e class to edit for create command
	 */
	public static EClass getEClassToEditForCreateCommand(EditPolicy editPolicy) {
		EObject element = getCanvasElement(editPolicy);
		return element != null ? element.eClass() : null;
	}

}
