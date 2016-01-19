/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop)
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

public class ModelInitializerRegistry {
	/**
	 * The extension point identifier
	 */
	private final String extensionPointID = "es.cv.gvcase.mdt.common.modelInitializer";

	/**
	 * HashMap for Model ID -> Model Initializer
	 */
	private HashMap<String, IModelInitializer> initializers;

	/**
	 * Gets the instance for this registry
	 * 
	 * @return
	 */
	public static ModelInitializerRegistry getInstance() {
		return new ModelInitializerRegistry();
	}

	/**
	 * Executes the initialization of the model for the given model ID and the
	 * given eObject
	 * 
	 * @param modelID
	 * @param eObject
	 * @return
	 */
	public EObject init(String modelID, EObject eObject) {
		if (initializers == null) {
			parseInitializers();
		}

		IModelInitializer initializer = initializers.get(modelID);
		if (initializer != null) {
			return initializer.init(eObject);
		} else {
			return eObject;
		}
	}

	/**
	 * Parse the initializers from the extension point
	 */
	private void parseInitializers() {
		initializers = new HashMap<String, IModelInitializer>();
		parseInitializersFromExtensionPoint();
	}

	/**
	 * Parse the initializers from the extension point and store them
	 */
	private void parseInitializersFromExtensionPoint() {
		ExtensionPointParser extensionPointParser = new ExtensionPointParser(
				extensionPointID, new Class[] { ModelInitializer.class });

		HashMap<String, List<InternalModelInitializer>> inits = new HashMap<String, List<InternalModelInitializer>>();
		for (Object o : extensionPointParser.parseExtensionPoint()) {
			if (o instanceof ModelInitializer) {
				ModelInitializer init = (ModelInitializer) Platform
						.getAdapterManager().getAdapter(o,
								ModelInitializer.class);
				if (init.modelID == "" || init.initializerClass == null) {
					continue;
				}

				if (!inits.containsKey(init.modelID)) {
					List<InternalModelInitializer> list = new ArrayList<InternalModelInitializer>();
					list.add(new InternalModelInitializer(
							(IModelInitializer) init.initializerClass, init
									.getPriorityValue()));
					inits.put(init.modelID, list);
				} else {
					inits.get(init.modelID).add(
							new InternalModelInitializer(
									(IModelInitializer) init.initializerClass,
									init.getPriorityValue()));
				}
			}
		}

		for (Entry<String, List<InternalModelInitializer>> entry : inits
				.entrySet()) {
			if (entry.getValue().size() == 0) {
				continue;
			}

			Collections.sort(entry.getValue(), new InitializerComparator());
			initializers.put(entry.getKey(),
					entry.getValue().get(0).initializer);
		}
	}

	/**
	 * A class to store the initializer and its priority
	 * 
	 * @author mgil
	 */
	private class InternalModelInitializer {
		public IModelInitializer initializer;
		public int priority;

		public InternalModelInitializer(IModelInitializer initializer,
				int priority) {
			this.initializer = initializer;
			this.priority = priority;
		}
	}

	/**
	 * A class to compare the initializers by its priority
	 * 
	 * @author mgil
	 */
	private class InitializerComparator implements
			Comparator<InternalModelInitializer> {
		public int compare(InternalModelInitializer o1,
				InternalModelInitializer o2) {
			if (o1.priority < o2.priority) {
				return 1;
			} else if (o1.priority == o2.priority) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}
