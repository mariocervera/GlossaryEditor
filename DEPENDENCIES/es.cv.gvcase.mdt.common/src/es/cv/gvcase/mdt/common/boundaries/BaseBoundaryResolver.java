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
package es.cv.gvcase.mdt.common.boundaries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.gmf.runtime.notation.View;

/**
 * An {@link IBoundaryResolver} used in the {@link DefaultStorageStrategy} in
 * the MOSKitt Artifacts Store.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class BaseBoundaryResolver implements IBoundaryResolver {

	private static IBoundaryResolver INSTANCE = null;

	private BaseBoundaryResolver() {

	}

	public static IBoundaryResolver getDefault() {
		if (INSTANCE == null) {
			INSTANCE = new BaseBoundaryResolver();
		}
		return INSTANCE;
	}

	public void manageBoundary(EObject eObject) {
		if (eObject == null) {
			return;
		}
		if (eObject instanceof View) {
			eObject = ((View) eObject).getElement();
		}
		// get all the EObjects that can be referenced
		List<EObject> referencableElements = new ArrayList<EObject>();
		getAllEObjects(eObject, referencableElements);
		// clean those EReferences that refer to elements that cannot be
		// referenced
		cleanReferencingFeatures(eObject, referencableElements);
	}

	protected void getAllEObjects(EObject eObject, List<EObject> eObjects) {
		if (eObject != null && eObject instanceof EAnnotation == false) {
			eObjects.add(eObject);
			for (EObject child : eObject.eContents()) {
				getAllEObjects(child, eObjects);
			}
		}
	}

	protected void cleanReferencingFeatures(EObject eObject,
			List<EObject> referencableElements) {
		if (eObject == null || eObject instanceof EAnnotation) {
			return;
		}
		for (EReference eReference : eObject.eClass().getEAllReferences()) {
			cleanEReference(eObject, eReference, referencableElements);
		}
		for (EObject child : eObject.eContents()) {
			cleanReferencingFeatures(child, referencableElements);
		}
	}

	protected void cleanEReference(EObject eObject, EReference eReference,
			List<EObject> referencableElements) {
		// only check those EReferences that are not containers
		if (eObject != null && eReference != null
				&& !eReference.isContainment() && !eReference.isDerived()
				&& eReference.isChangeable()) {
			Object value = eObject.eGet(eReference);
			if (value instanceof Collection) {
				// in case of collection, check all the values in the reference
				Collection values = (Collection) value;
				Collection newValue = new ArrayList();
				boolean valueChanged = false;
				for (Object oneValue : values) {
					if (oneValue instanceof EObject
							&& !referencableElements
									.contains((EObject) oneValue)) {
						valueChanged = false;
					} else {
						newValue.add(oneValue);
					}
				}
				if (newValue.size() > 0) {
					setReferenceListNewValue(values, newValue);
				}
			} else if (value instanceof EObject) {
				// check if the references value need to be unset
				if (!referencableElements.contains(value)) {
					eObject.eUnset(eReference);
				}
			}
		}
	}

	protected void setReferenceListNewValue(Collection oldValue,
			Collection newValue) {
		//
		Collection toRemove = new ArrayList();
		Collection toAdd = new ArrayList();
		// elements to remove from oldValue list
		for (Object o : oldValue) {
			if (!newValue.contains(o)) {
				toRemove.add(o);
			}
		}
		// elements to add to olsValue list
		for (Object o : newValue) {
			if (!oldValue.contains(o)) {
				toAdd.add(o);
			}
		}
		// apply changes
		oldValue.removeAll(toRemove);
		newValue.addAll(toAdd);
		//
	}

}
