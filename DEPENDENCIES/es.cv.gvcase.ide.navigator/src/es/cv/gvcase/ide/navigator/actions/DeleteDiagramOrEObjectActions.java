/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;

/**
 * The Class DeleteDiagramOrEObjectActions.
 * 
 * @deprecated
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DeleteDiagramOrEObjectActions extends MOSKittCommonActionProvider {

	/** The delete action. */
	DeleteDiagramOrEObjectAction deleteAction = null;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				getDeleteAction());
	}

	/**
	 * Gets the delete action.
	 * 
	 * @return the delete action
	 */
	private DeleteDiagramOrEObjectAction getDeleteAction() {
		if (deleteAction == null) {
			deleteAction = new DeleteDiagramOrEObjectAction(true);
		}
		return deleteAction;
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
			getDeleteAction()
					.selectionChanged((IStructuredSelection) selection);
		}
	}

}
