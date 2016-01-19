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

import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.ui.INewWizard;

import es.cv.gvcase.mdt.common.model.TypesGroup;

/**
 * New diagram file and model wizard.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface INewModelDiagramWizard extends INewWizard {

	// //
	// Diagram kind
	// //

	/**
	 * Get the diagram kind that will be initialized.
	 */
	String getDiagramKind();

	/**
	 * Set the diagram kind that will be initialized.
	 * 
	 * @param diagramKind
	 */
	void setDiagramKind(String diagramKind);

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

	// //
	// Basic types selected
	// //

	/**
	 * Gets the basic types the user has selected.
	 */
	Collection<TypesGroup> getSelectedBasicTypes();

	/**
	 * Sets the basic types the user has selected.
	 */
	void setSelectedBasicTypes(Collection<TypesGroup> basicTypes);

}
