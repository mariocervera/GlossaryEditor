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
package es.cv.gvcase.mdt.common.part;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.jface.wizard.IWizard;

/**
 * Provides several methods to provide information for Diagram Initialization in
 * MOSKitt editors.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface INewDiagramFileWizard extends IWizard {

	// //
	// Root element
	// //

	/**
	 * Gets the model element that will be the root element for the new Diagram.
	 */
	EObject getRootModelElement();

	/**
	 * Sets the model element that will be the root element for the new Diagram.
	 * 
	 * @param rootElement
	 */
	void setRootModelElement(EObject rootElement);

	// //
	// Selected elements for diagram
	// //

	/**
	 * Gets the collection of selected elements that must appear in the diagram.
	 */
	Collection<EObject> getSelectedElementForDiagram();

	/**
	 * Sets the collection of selected elements that must appear in the diagram.
	 * 
	 * @param selectedEObjects
	 */
	void setSelectedElementForDiagram(Collection<EObject> selectedEObjects);

	// //
	// diagram kind
	// //
	
	String getDiagramKind();
	
	void setDiagramKind(String kind);
	
	// //
	// Preferences hint
	// //

	/**
	 * Get the preferences hint to use upon diagram initialization.
	 */
	PreferencesHint getPreferencesHint();

	/**
	 * Set the preferences hint to be used upon diagram initialization.
	 * 
	 * @param preferencesHint
	 */
	void setPreferencesHint(PreferencesHint preferencesHint);

}
