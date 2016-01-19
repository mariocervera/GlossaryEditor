/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Integranova) - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Helper class to store the information to create the Delete Button in the
 * Master composite
 * 
 * @author mgil
 */
public class DeleteButtonInfo {
	public String text;
	protected List<EClass> availableFor;
	protected boolean checkSupertypes;

	/**
	 * Create a Button Info helper with a whole check of the SuperClasses types
	 */
	public DeleteButtonInfo(String text, List<EClass> availableFor) {
		this(text, availableFor, true);
	}

	public DeleteButtonInfo(String text, List<EClass> availableFor,
			boolean checkSupertypes) {
		this.text = text;
		this.availableFor = availableFor;
		this.checkSupertypes = checkSupertypes;
	}

	/**
	 * Check if the Button will be available for the specified selection
	 */
	public boolean isEnabledFor(ISelection selection) {
		if (customCheck(selection)) {
			return true;
		}

		if (!(selection instanceof StructuredSelection)) {
			return false;
		}

		StructuredSelection ss = (StructuredSelection) selection;
		if (!(ss.getFirstElement() instanceof EObject)) {
			return false;
		}

		EObject eo = (EObject) ss.getFirstElement();
		return isEnabledFor(eo);
	}

	protected boolean isEnabledFor(EObject eObject) {
		if (!availableFor.contains(eObject.eClass())) {
			if (checkSupertypes) {
				for (EClass ec : eObject.eClass().getEAllSuperTypes()) {
					if (availableFor.contains(ec)) {
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Override this method if a customized check is preferred
	 */
	protected boolean customCheck(ISelection selection) {
		return false;
	}
}
