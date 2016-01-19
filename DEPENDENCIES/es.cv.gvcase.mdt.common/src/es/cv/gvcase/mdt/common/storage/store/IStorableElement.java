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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.Diagram;

/**
 * A storable element can be stored in the common MOSKitt storage area. <br>
 * Elements stored in the common storage area are available as template elements
 * to all accessible MOSKitt editors.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface IStorableElement extends IAdaptable {

	// //
	// extended feature ids.
	// //

	/**
	 * Stored element name
	 */
	public static final String StoredNameIdentifier = "es.cv.gvcase.mdt.common.storage.storable.name";

	/**
	 * Stored element description
	 */
	public static final String StoredDescriptionIdentifier = "es.cv.gvcase.mdt.common.storage.storable.description";

	/**
	 * Stored element nsUri
	 */
	public static final String StoredNsUriIdentifier = "es.cv.gvcase.mdt.common.storage.storable.nsUri";

	/**
	 * Stored element identifier
	 */
	public static final String StoreIdentifierIdentifier = "es.cv.gvcase.mdt.common.storage.storable.Identifier";

	/**
	 * Stored element helper
	 */
	public static final String StoredElementHelper = "es.cv.gvcase.mdt.common.storage.storable.helper";

	/**
	 * Stored element related diagrams
	 */
	public static final String storedElementRelatedDiagrams = "es.cv.gvcase.mdt.common.storage.storable.relatedDiagrams";

	/**
	 * Category of stored element.
	 */
	public static final String StoredElementCategory = "es.cv.gvcase.mdt.common.storage.storable.category";

	public static final String DefaultCategoryString = "Other";
	
	// //
	//
	// //

	boolean canDelete();

	void setCanDelete(boolean canDelete);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getNsUri();

	void setNsUri(String nsUri);

	String getIdentifier();

	void setIdentifier(String identifier);

	String getCategory();

	void setCategory(String category);

	EObject getEObject();

	void setEobject(EObject eObject);

	List<EObject> getRelatedDiagrams();

	void setRelatedDiagrams(List<Diagram> diagrams);

}
