/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Miguel Llacer San Fernando (Prodevelop) - Initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.emf.common.util;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import es.cv.gvcase.emf.common.part.PriorityAdapterFactoryRegistry;

public class EMFUtil {

	protected static AdapterFactoryLabelProvider adapterFactoryLabelProvider = null;

	public static AdapterFactoryLabelProvider getAdapterFactoryLabelProvider() {
		if (adapterFactoryLabelProvider == null) {
			adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(
					PriorityAdapterFactoryRegistry.getInstance());
		}
		return adapterFactoryLabelProvider;
	}

	protected static AdapterFactoryContentProvider adapterFactoryContentProvider = null;

	public static AdapterFactoryContentProvider getAdapterFactoryContentProvider() {
		if (adapterFactoryContentProvider == null) {
			adapterFactoryContentProvider = new AdapterFactoryContentProvider(
					PriorityAdapterFactoryRegistry.getInstance());
		}
		return adapterFactoryContentProvider;
	}

}
