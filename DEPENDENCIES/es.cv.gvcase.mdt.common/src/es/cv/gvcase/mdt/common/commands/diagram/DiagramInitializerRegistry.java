/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands.diagram;

import java.util.HashMap;
import java.util.Map;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;
import es.cv.gvcase.mdt.common.provider.IDiagramInitializer;

/**
 * A registry of {@link IDiagramInitializer}s sorted by diagram kind.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class DiagramInitializerRegistry {

	public class DiagramInitializer {
		public String label;
		public String diagramID;
		public Object initializer;
	}

	// //
	// Singleton
	// //

	private DiagramInitializerRegistry() {
	}

	protected static DiagramInitializerRegistry INSTANCE = null;

	public static DiagramInitializerRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DiagramInitializerRegistry();
		}
		return INSTANCE;
	}

	// //
	// Map registry
	// //

	public static final String ExtensionPointID = "es.cv.gvcase.mdt.common.diagramInitializer";

	protected Map<String, IDiagramInitializer> mapDiagramID2Initializer = new HashMap<String, IDiagramInitializer>();

	protected void readExtensionPoint() {
		// clear the storing map and the list to start each time from scratch
		mapDiagramID2Initializer.clear();
		// parse the extension point using classes defined inside this class
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID, new Class[] { DiagramInitializer.class },
				this);
		// loop all over the contributions
		DiagramInitializer initializer = null;
		for (Object o : parser.parseExtensionPoint()) {
			if (o instanceof DiagramInitializer) {
				initializer = (DiagramInitializer) o;
				if (initializer.initializer instanceof IDiagramInitializer) {
					mapDiagramID2Initializer.put(initializer.diagramID,
							(IDiagramInitializer) initializer.initializer);
				}
			} else {
				initializer = null;
			}
		}
	}

	// //
	// Utility methods
	// //

	/**
	 * Retrieves the {@link IDiagramInitializer} for the given diagram kind.
	 */
	public IDiagramInitializer getDiagramInitializer(String diagramKind) {
		if (diagramKind == null) {
			return null;
		} else {
			if (mapDiagramID2Initializer.containsKey(diagramKind)) {
				return mapDiagramID2Initializer.get(diagramKind);
			}
		}
		return null;
	}

	/**
	 * Returns all the {@link IDiagramInitializer}s.
	 * 
	 * @return
	 */
	public Map<String, IDiagramInitializer> getAllDiagramInitializers() {
		readExtensionPoint();
		return mapDiagramID2Initializer;
	}

}
