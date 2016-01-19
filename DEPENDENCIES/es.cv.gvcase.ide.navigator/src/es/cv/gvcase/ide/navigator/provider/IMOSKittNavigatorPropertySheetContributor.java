/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.provider;

import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.ide.navigator.view.MOSKittCommonNavigator;

/**
 * Helper interface to contribute {@link TabbedPropertySheetPage}s to
 * {@link MOSKittCommonNavigator}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public interface IMOSKittNavigatorPropertySheetContributor {
	
	/**
	 * Gets the contributor id.
	 * 
	 * @return the contributor id
	 */
	String getContributorID();
	
	/**
	 * Checks for property sheet page.
	 * 
	 * @return true, if successful
	 */
	boolean hasPropertySheetPage();
	
	/**
	 * Gets the property sheet page.
	 * 
	 * @return the property sheet page
	 */
	TabbedPropertySheetPage getPropertySheetPage();
}
