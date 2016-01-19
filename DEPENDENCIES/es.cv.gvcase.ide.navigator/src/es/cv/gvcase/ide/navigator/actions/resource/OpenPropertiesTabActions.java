/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.jface.action.IMenuManager;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;


public class OpenPropertiesTabActions extends MOSKittCommonActionProvider {

	/** The open action. */
	private OpenPropertiesTabAction showPropertiesAction = null;

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(getAction());
	}

	/**
	 * Gets the open action.
	 * 
	 * @return the open action
	 */
	protected OpenPropertiesTabAction getAction() {
		if (showPropertiesAction == null) {
			showPropertiesAction = new OpenPropertiesTabAction();
		}
		return showPropertiesAction;
	}


	@Override
	public void updateActionBars() {
		super.updateActionBars();
	}
}
