/*******************************************************************************
 * Copyright (c) 2005 AIRBUS FRANCE. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright (c) of modifications Conselleria de Infraestructuras y Transporte, 
 * Generalitat de la Comunitat Valenciana a. All rights reserved. Modifications 
 * are made available under the terms of the Eclipse Public License v1.0.
 * 
 * Contributors: David Sciamma (Anyware Technologies), Mathieu Garcia (Anyware
 * Technologies), Jacques Lescot (Anyware Technologies) - initial API and
 * implementation
 * 
 * Gabriel Merin Cubero (Integranova) - Expanded functionallity
 * Mario Cervera Ubeda (Integranova) - Modified to adapt to the datatools metamodel
 ******************************************************************************/

package es.cv.gvcase.mdt.common.actions;

import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;


// TODO: Auto-generated Javadoc
/**
 * This helper class is used to get/set the name of a NamedElement EObject.
 * 
 * Modified 21 september 2007
 * 
 * @author <a href="mailto:gmerin@integranova.com">Gabriel Merin</a>
 */
public class LabelHelper {
	
	/** The singleton. */
	public static final LabelHelper INSTANCE = new LabelHelper();

	/**
	 * Initialize the name of a child EObject contained in a parent EObject.
	 * 
	 * @param parentEObject the parent EObject to start searching
	 * @param childEObject the child EObject whose name should be initialized
	 */
	public void initName(EObject parentEObject, EObject childEObject) {
		if ((childEObject != null) && (childEObject instanceof ENamedElement)) {
			String name = findName(parentEObject, (ENamedElement) childEObject);

			// Either delegate the call or return nothing.
			EAttribute att = null;
			att = EcorePackage.eINSTANCE.getENamedElement_Name();

			if (att != null
					&& ((EDataType) att.getEType()).getInstanceClass().equals(
							String.class)) {
				childEObject.eSet(att, name);
			}
		}
	}

	/**
	 * This returns a name to give to given EObject.
	 * 
	 * @param parentEObject the parent EObject
	 * @param childEObject the new EObject to add
	 * 
	 * @return the name
	 */
	public String findName(EObject parentEObject, ENamedElement childEObject) {
		return findName(parentEObject, childEObject.eClass());
	}

	/**
	 * This returns a name to give to a new EObject.
	 * 
	 * @param parentEObject the parent EObject
	 * @param childEClass the new EObject to add
	 * 
	 * @return the name
	 */
	public String findName(EObject parentEObject, EClass childEClass) {
		int cpt = 1;
		while (!isNameAvailable(parentEObject, childEClass, cpt)) {
			cpt++;
		}
		return childEClass.getName() + cpt;
	}

	/**
	 * Check if a name is available.
	 * 
	 * @param parentEObject the parent EObject
	 * @param childEClass EClass of the new EObject to add
	 * @param currentCpt the current cpt
	 * 
	 * @return true if the name is available
	 */
	private boolean isNameAvailable(EObject parentEObject, EClass childEClass,
			int currentCpt) {
		EList list = parentEObject.eContents();
		for (Iterator i = list.iterator(); i.hasNext();) {
			EObject child = (EObject) i.next();

			// check if the current child is the same type of the childEObject
			if (childEClass.getName().equals(child.eClass().getName())) {
				String nameToMatch = child.eClass().getName() + currentCpt;
				if (nameToMatch.equals(((ENamedElement) child).getName())) {
					return false;
				}
			}
		}
		return true;
	}

}
