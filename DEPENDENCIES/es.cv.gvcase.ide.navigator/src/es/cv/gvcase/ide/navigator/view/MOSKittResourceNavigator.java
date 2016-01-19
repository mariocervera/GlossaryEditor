/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API implementation.
 * 				 Marc Gil Sendra (Prodevelop) - Improve for avoid stupid refreshes
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.view;

import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * A specialization of {@link MOSKittCommonNavigator} to show workspace
 * resources in an explorer view.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es>Francisco Javier Cano Muñoz</a>
 */
public class MOSKittResourceNavigator extends MOSKittCommonNavigator {

	/**
	 * Make sure no {@link IPropertySheetPage} is given.
	 */
	@Override
	public Object getAdapter(Class adapter) {
		if (IPropertySheetPage.class.equals(adapter)) {
			return null;
		} else {
			return super.getAdapter(adapter);
		}
	}

}
