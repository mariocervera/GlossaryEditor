/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial API implementation
 * [03/04/08] Francisco Javier Cano Muñoz (Prodevelop) - adaptation to Common Navigator Framework
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;

/**
 * Action provider for {@link OpenDiagramInNewEditorAction}.
 * 
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class OpenDiagramInNewEditorActions extends MOSKittCommonActionProvider {

	/** The open action. */
	private OpenDiagramInNewEditorAction openDiagramAction = null;

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(getOpenInNewEditorAction());
	}

	/**
	 * Gets the open action.
	 * 
	 * @return the open action
	 */
	protected OpenDiagramInNewEditorAction getOpenInNewEditorAction() {
		if (openDiagramAction == null) {
			openDiagramAction = new OpenDiagramInNewEditorAction();
		}
		return openDiagramAction;
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
			getOpenInNewEditorAction().selectionChanged(
					(IStructuredSelection) selection);
		}
	}

}
