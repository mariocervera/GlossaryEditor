/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.ICommonActionConstants;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;

/**
 * Provides the CreateDiagramAction to the global handlers of the navigator.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class CreateDiagramActions extends MOSKittCommonActionProvider {

	/** The open action. */
	private CreateDiagramAction createDiagramAction = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars
	 * )
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		// //
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
				getCreateAction());
	}

	/**
	 * Gets the create action.
	 * 
	 * @return the create action
	 */
	protected CreateDiagramAction getCreateAction() {
		if (createDiagramAction == null) {
			createDiagramAction = new CreateDiagramAction();
		}
		return createDiagramAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
	 */
	@Override
	public void updateActionBars() {
		super.updateActionBars();
		// //
		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection) {
			getCreateAction()
					.selectionChanged((IStructuredSelection) selection);
		}
	}

}
