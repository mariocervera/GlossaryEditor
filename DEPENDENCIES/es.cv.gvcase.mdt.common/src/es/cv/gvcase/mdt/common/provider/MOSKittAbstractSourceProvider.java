/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) [mgil@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;

/**
 * This class is a Source Provider for MOSKitt that allow to create Services to
 * get any value. When another service is created using this class, a new ID
 * should be added, and subClasses should use this new ID.
 * 
 * @author <a href="mailto:mgil@prodevelop.es">Marc Gil Sendra</a>
 */
public abstract class MOSKittAbstractSourceProvider extends
		AbstractSourceProvider {
	public final static String STORAGE_SERVICE_ID = "es.cv.gvcase.mdt.common.storage.service";

	public void dispose() {
	}

	public Map getCurrentState() {
		Map map = new HashMap();
		return map;
	}

	/**
	 * Get the desired values for this Service
	 */
	public abstract Object getValues();
}
