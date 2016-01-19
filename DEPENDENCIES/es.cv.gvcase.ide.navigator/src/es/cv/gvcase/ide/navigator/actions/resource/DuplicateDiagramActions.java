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
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;

/**
 * Specialization of {@link MOSKittCommonActionProvider} to contribute the
 * {@link DuplicateDiagramAction} global handler.
 * 
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DuplicateDiagramActions extends MOSKittCommonActionProvider {

	/** The open action. */
	private DuplicateDiagramAction duplicateAction = null;

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(getDuplicateAction());
	}

	/**
	 * Gets the open action.
	 * 
	 * @return the open action
	 */
	protected DuplicateDiagramAction getDuplicateAction() {
		if (duplicateAction == null) {
			duplicateAction = new DuplicateDiagramAction();
		}
		return duplicateAction;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
	 */
	@Override
	public void updateActionBars() {
		super.updateActionBars();
		// //
		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection) {
			getDuplicateAction().selectionChanged((IStructuredSelection) selection);
		}
	}

}
