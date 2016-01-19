/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;

/**
 * This registry provides the available diagrams file extensions from MOSKit
 * that have been contributed via the 'org.eclipse.iu.editors' extension point.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class MOSKittDiagramsFileExtensionRegistry {

	// //
	// Singleton
	// //

	private static MOSKittDiagramsFileExtensionRegistry INSTANCE = null;

	/**
	 * Gets the singleton instance of this registry.
	 * 
	 * @return
	 */
	public static MOSKittDiagramsFileExtensionRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MOSKittDiagramsFileExtensionRegistry();
		}
		return INSTANCE;
	}

	private MOSKittDiagramsFileExtensionRegistry() {

	}

	// //
	// Extension point reading
	// //

	public static final String ExtensionPointID = "org.eclipse.ui.editors"; //$NON-NLS-1$

	protected List<String> availableDiagrmaFileExtensions = null;

	protected void readExtensionPoint() {
		if (availableDiagrmaFileExtensions == null) {
			availableDiagrmaFileExtensions = new ArrayList<String>();
		}
		availableDiagrmaFileExtensions.clear();
		//
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID, new Class[] { editor.class }, this);
		editor editorContribution = null;
		for (Object o : parser.parseExtensionPoint()) {
			if (o instanceof editor) {
				editorContribution = (editor) o;
				if (MOSKittMultiPageEditor.MOSKittMultiPageEditorID
						.equals(editorContribution.id)) {
					availableDiagrmaFileExtensions
							.add(editorContribution.extensions.trim());
				}
			}
		}
	}

	// //
	// Public query methods
	// //

	/**
	 * Checks whether the given file extension is one of the available MOSKitt
	 * diagram file extensions.
	 */
	public boolean isMOSKittDiagramFileExtension(String fileExtension) {
		if (fileExtension == null || fileExtension.trim().length() <= 0) {
			return false;
		}
		if (availableDiagrmaFileExtensions == null) {
			readExtensionPoint();
		}
		if (availableDiagrmaFileExtensions.contains(fileExtension.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * Provides all the file extensions from MOSKitt diagram editors.
	 * 
	 * @return
	 */
	public List<String> getAllMOSKittDiagramFileExtensions() {
		if (availableDiagrmaFileExtensions == null) {
			readExtensionPoint();
		}
		return Collections.unmodifiableList(availableDiagrmaFileExtensions);
	}

	// //
	// Stub class for extension point parsing
	// //

	public class editor {
		public String id;
		public String extensions;
	}

}
