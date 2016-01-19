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
package es.cv.gvcase.mdt.common.storage.store;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.util.CopyUtils;

/**
 * A basic implementation of {@link IStorableElement} relying on
 * {@link ExtendedFeatureElement}s.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * @see IStorableElement
 * 
 */
public class StorableElement implements IStorableElement {

	// //
	// local variables
	// //

	protected EModelElement element;

	protected ExtendedFeatureElement extendedElement;

	protected boolean canDelete = true;

	// //
	// constructors
	// //

	public StorableElement(EObject eObject) {
		this((EModelElement) eObject);
	}

	public StorableElement(EModelElement element) {
		this.element = element;
		markStoredElement(element);
	}

	protected void markStoredElement(EModelElement element) {
		if (element == null) {
			return;
		}
		if (element instanceof View) {
			if (!(((View) element).getElement() instanceof EModelElement)) {
				return;
			} else {
				addHelperEAnnotation((EModelElement) ((View) element)
						.getElement());
			}
		}
		addHelperEAnnotation(element);
	}

	protected void addHelperEAnnotation(EModelElement element) {
		if (element == null || element instanceof EAnnotation) {
			return;
		}
		if (element.getEAnnotation(StoredElementHelper) == null) {
			EAnnotation eAnnotation = EcoreFactory.eINSTANCE
					.createEAnnotation();
			eAnnotation.setSource(StoredElementHelper);
			element.getEAnnotations().add(eAnnotation);
		}
		Iterator<EObject> iterator = element.eContents().iterator();
		while (iterator.hasNext()) {
			EObject next = iterator.next();
			if (next instanceof EModelElement) {
				addHelperEAnnotation((EModelElement) next);
			}
		}
	}

	// //
	// utility constructor
	// //

	public static StorableElement fromEObject(EObject eObject) {
		if (eObject instanceof EModelElement) {
			EObject newElement = CopyUtils.copyWithDiagrams(eObject);
			// EObject newElement = CopyUtils.copyEObject(eObject);
			return new StorableElement((EModelElement) newElement);
		}
		return null;
	}

	public static StorableElement fromStoredEObject(EObject eObject) {
		if (eObject instanceof EModelElement == false) {
			return null;
		}
		StorableElement element = new StorableElement(eObject);
		if (element == null || element.getName() == null) {
			return null;
		}
		return element;
	}

	protected ExtendedFeatureElement asExtendedFeatureElement() {
		if (extendedElement == null) {
			extendedElement = ExtendedFeatureElementFactory.getInstance()
					.asExtendedFeatureElement(element);
		}
		return extendedElement;
	}

	// //
	// IStorableElement methods
	// //

	public String getDescription() {
		return asExtendedFeatureElement()
				.getString(StoredDescriptionIdentifier);
	}

	public EObject getEObject() {
		return this.element;
	}

	public String getName() {
		return asExtendedFeatureElement().getString(StoredNameIdentifier);
	}

	public String getNsUri() {
		return asExtendedFeatureElement().getString(StoredNsUriIdentifier);
	}

	public String getIdentifier() {
		return asExtendedFeatureElement().getString(StoreIdentifierIdentifier);
	}

	public String getCategory() {
		String category = asExtendedFeatureElement().getString(
				StoredElementCategory);
		if (category == null || category.length() <= 0) {
			category = DefaultCategoryString;
		}
		return category;
	}

	public List<EObject> getRelatedDiagrams() {
		return asExtendedFeatureElement().getReferenceList(
				storedElementRelatedDiagrams);
	}

	public void setDescription(String description) {
		asExtendedFeatureElement().setValue(StoredDescriptionIdentifier,
				description);
	}

	public void setName(String name) {
		asExtendedFeatureElement().setValue(StoredNameIdentifier, name);
	}

	public void setNsUri(String nsUri) {
		asExtendedFeatureElement().setValue(StoredNsUriIdentifier, nsUri);
	}

	public void setIdentifier(String identifier) {
		asExtendedFeatureElement().setValue(StoreIdentifierIdentifier,
				identifier);
	}

	public void setCategory(String category) {
		asExtendedFeatureElement().setValue(StoredElementCategory, category);
	}

	public void setEobject(EObject eObject) {
		if (eObject instanceof EModelElement) {
			this.element = (EModelElement) eObject;
		} else {
			throw new IllegalArgumentException(
					"An EmodelElement must be provided");
		}
	}

	public void setRelatedDiagrams(List<Diagram> diagrams) {
		asExtendedFeatureElement().setValue(storedElementRelatedDiagrams,
				diagrams);
	}

	public boolean canDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	// //
	// IAdaptable
	// //

	public Object getAdapter(Class adapter) {
		if (EObject.class.equals(adapter)) {
			return getEObject();
		}
		return null;
	}

	// //
	// Equals
	// //

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IStorableElement) {
			IStorableElement otherElement = (IStorableElement) obj;
			if (getName() != null && getName().equals(otherElement.getName())) {
				if (getIdentifier() != null
						&& getIdentifier().equals(otherElement.getIdentifier())) {
					return true;
				}
			}
			return false;
		}
		return super.equals(obj);
	}

}
