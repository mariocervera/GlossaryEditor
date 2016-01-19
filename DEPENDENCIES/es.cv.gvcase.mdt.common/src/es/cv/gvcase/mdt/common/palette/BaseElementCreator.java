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
package es.cv.gvcase.mdt.common.palette;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Default implementation of {@link ElementCreator} for a TemplateTool. <br>
 * This default creation creates the elements using the proper {@link EFactory}
 * and stores them in the given containment features of the given parent
 * elements.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see ElementCreator
 * @see TemplateToolRegistry
 */
public class BaseElementCreator implements ElementCreator {

	protected static BaseElementCreator INSTANCE = null;

	public static BaseElementCreator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BaseElementCreator();
		}
		return INSTANCE;
	}

	protected EFactory baseEFactory = null;

	public EFactory getBaseEFactory() {
		return baseEFactory;
	}

	public void setBaseEFactory(EFactory baseEFactory) {
		this.baseEFactory = baseEFactory;
	}

	protected EPackage baseEPackage = null;

	public EPackage getBaseEPackage() {
		return baseEPackage;
	}

	public void setBaseEPackage(EPackage baseEPackage) {
		this.baseEPackage = baseEPackage;
	}

	public EObject createElement(EObject parent, Element element) {
		if (element != null) {
			// get the proper factory
			EFactory eFactory = getBaseEFactory();
			// create the new EObject
			EObject eObject = eFactory.create(getEclassFor(element));
			// search for the reference that must contain this new eObject in
			// its parent
			EReference reference = getEReferenceFor(parent.eClass(), element);
			if (reference != null) {
				Object value = parent.eGet(reference, true);
				if (value instanceof List) {
					// add the new EObject to the reference if it's a list
					((List) value).add(eObject);
				} else {
					// or set it if it's a single-valued feature
					parent.eSet(reference, eObject);
				}
			}
			// return the created EObject
			return eObject;
		}
		return null;
	}

	/**
	 * Get the {@link EClass} for the given element.
	 * 
	 * @param element
	 * @return
	 */
	protected EClass getEclassFor(Element element) {
		EPackage ePackage = getEPackageFor(element);
		EClassifier classifier = ePackage.getEClassifier(element.name);
		EClass eClass = null;
		if (classifier instanceof EClass) {
			eClass = (EClass) classifier;
		}
		return eClass;
	}

	protected EReference getEReferenceFor(EClass eClass, Element element) {
		if (eClass == null || element == null || element.containment == null) {
			return null;
		}
		EReference reference = null;
		// search the reference by name
		for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
			if (feature instanceof EReference
					&& element.containment.equals(feature.getName())) {
				if (((EReference) feature).isContainment()) {
					reference = (EReference) feature;
				}
			}
		}
		if (reference == null) {
			EClass elementEClass = getEclassFor(element);
			// search the reference by containment feature
			for (EStructuralFeature feature : eClass
					.getEAllStructuralFeatures()) {
				if (feature instanceof EReference
						&& element.containment.equals(feature.getName())) {
					if (((EReference) feature).isContainment()) {
						if (feature.getEType() != null
								&& feature.getEType().equals(elementEClass)) {
							reference = (EReference) feature;
						}
					}
				}
			}
		}
		return reference;
	}

	/**
	 * Get the {@link EPackage} for the given element. Can be different from the
	 * root EPackage if the element specifies a different nsURI.
	 */
	protected EPackage getEPackageFor(Element element) {
		EPackage ePackage = getBaseEPackage();
		if (element.packageUri != null
				&& !element.packageUri.equals(ePackage.getNsURI())) {
			ePackage = EPackage.Registry.INSTANCE
					.getEPackage(element.packageUri);
		}
		return ePackage;
	}

	/**
	 * Get the {@link EFactory} for the given element. Can be different from the
	 * root EFactory if the element specifies a different nsURI.
	 * 
	 * @param element
	 * @return
	 */
	protected EFactory getEFactoryFor(Element element) {
		EFactory eFactory = getBaseEFactory();
		if (element.packageUri != null
				&& !element.packageUri
						.equals(eFactory.getEPackage().getNsURI())) {
			eFactory = EPackage.Registry.INSTANCE
					.getEFactory(element.packageUri);
		}
		return eFactory;
	}

}
