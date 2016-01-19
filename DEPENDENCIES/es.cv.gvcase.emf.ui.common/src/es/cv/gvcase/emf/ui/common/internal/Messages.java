/*******************************************************************************
* Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
* de la Comunitat Valenciana. All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*  Miguel Llacer (Prodevelop) [mllacer@prodevelop.es] - initial API implementation
******************************************************************************/
package es.cv.gvcase.emf.ui.common.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "es.cv.gvcase.emf.ui.common.internal.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

}
