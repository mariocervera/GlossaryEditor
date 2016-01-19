/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class ElementInDiagramsActions extends MOSKittCommonActionProvider {

	private ElementInDiagramsAction instance = null;

	private ElementInDiagramsAction getAction() {
		return instance == null ? instance = new ElementInDiagramsAction()
				: instance;
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(getAction());
	}

	@Override
	public void updateActionBars() {
		super.updateActionBars();
		// //
		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection) {
			getAction().selectionChanged((IStructuredSelection) selection);
		}
	}
}
