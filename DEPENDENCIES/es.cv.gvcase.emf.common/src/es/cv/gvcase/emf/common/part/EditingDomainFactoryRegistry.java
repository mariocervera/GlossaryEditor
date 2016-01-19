/*******************************************************************************
 * Copyright (c) 2008 - 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.emf.common.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain.Factory;

/**
 * Registry for the editing domain factory. <br>
 * Reads contributions from extensions and provides them to whomever asks.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see EditingDomainRegistry
 * 
 */
public class EditingDomainFactoryRegistry {

	// //
	// Singleton
	// //

	private static final EditingDomainFactoryRegistry INSTANCE = new EditingDomainFactoryRegistry();

	private EditingDomainFactoryRegistry() {
	}

	public static EditingDomainFactoryRegistry getInstance() {
		return INSTANCE;
	}

	// //
	// Read extension point's extensions
	// //

	private static final String ExtensionPointID = "es.cv.gvcase.emf.common.editingDomainFactory";

	protected String getExtensionPointID() {
		return ExtensionPointID;
	}

	private Map<String, EditingDomainFactory> mapPriority2EditingDomainFactory = null;

	protected void readExtensionPoint() {
		// initialize if necessary
		if (mapPriority2EditingDomainFactory == null) {
			mapPriority2EditingDomainFactory = new HashMap<String, EditingDomainFactory>();
		}
		// clear current map
		mapPriority2EditingDomainFactory.clear();
		// read extension point
		ExtensionPointParser parser = new ExtensionPointParser(
				getExtensionPointID(),
				new Class[] { EditingDomainFactory.class });
		EditingDomainFactory factory = null;
		for (Object object : parser.parseExtensionPoint()) {
			if (object instanceof EditingDomainFactory) {
				factory = (EditingDomainFactory) object;
				mapPriority2EditingDomainFactory.put(factory.priority, factory);
			}
		}
	}

	// //
	// Priorities
	// //

	private static final String Lowest = "Lowest";
	private static final String Low = "Low";
	private static final String Normal = "Normal";
	private static final String High = "High";
	private static final String Highest = "Highest";

	private static final String[] Priorities = new String[] { Highest, High,
			Normal, Low, Lowest };

	// //
	// Factory getter
	// //
	public Factory getFactory() {
		readExtensionPoint();
		for (String priority : Priorities) {
			if (mapPriority2EditingDomainFactory.containsKey(priority)) {
				Object object = mapPriority2EditingDomainFactory.get(priority);
				if (object instanceof EditingDomainFactory) {
					Object factoryCandidate = ((EditingDomainFactory) object).factoryClass;
					return factoryCandidate instanceof Factory ? (Factory) factoryCandidate
							: null;
				}
			}
		}
		return null;
	}

}
