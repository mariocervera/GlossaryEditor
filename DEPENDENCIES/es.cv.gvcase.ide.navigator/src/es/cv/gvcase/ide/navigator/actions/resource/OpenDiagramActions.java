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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.ICommonActionConstants;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;

/**
 * Specialization of {@link MOSKittCommonActionProvider} to contribute the
 * {@link OpenDiagramAction} global handler.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenDiagramActions extends MOSKittCommonActionProvider {

	/** The open action. */
	private OpenDiagramAction openAction = null;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		// //
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
				getOpenAction());
	}

	/**
	 * Gets the open action.
	 * 
	 * @return the open action
	 */
	protected OpenDiagramAction getOpenAction() {
		if (openAction == null) {
			openAction = new OpenDiagramAction();
		}
		return openAction;
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
			getOpenAction().selectionChanged((IStructuredSelection) selection);
		}
	}

}
