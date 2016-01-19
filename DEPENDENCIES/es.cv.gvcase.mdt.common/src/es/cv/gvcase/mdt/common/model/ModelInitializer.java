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

import java.util.HashMap;

public class ModelInitializer {

	/**
	 * The model ID where provides an initialization
	 */
	public String modelID;

	/**
	 * The initializer class
	 */
	public Object initializerClass;

	/**
	 * Lowest, Low, Medium, High, Highest
	 */
	public String priority;

	private HashMap<String, Integer> mapping;

	/**
	 * Return the int value for the stored priority
	 * 
	 * @return
	 */
	public int getPriorityValue() {
		return getMapping().containsKey(priority) ? getMapping().get(priority)
				.intValue() : -1;
	}

	private HashMap<String, Integer> getMapping() {
		if (mapping == null) {
			mapping = new HashMap<String, Integer>();
			mapping.put("Lowest", new Integer(0));
			mapping.put("Low", new Integer(1));
			mapping.put("Medium", new Integer(2));
			mapping.put("High", new Integer(3));
			mapping.put("Highest", new Integer(4));
		}

		return mapping;
	}
}
