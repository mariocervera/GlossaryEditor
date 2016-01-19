/***************************************************************************
 * Copyright (c) 2008 - 2011 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import java.util.List;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IWorkbenchPage;

import es.cv.gvcase.mdt.common.commands.RestoreViewCommand;
import es.cv.gvcase.mdt.common.commands.UpdateDiagramCommand;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Restores a view.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class RestoreViewAction extends DiagramAction {

	public RestoreViewAction(IWorkbenchPage page, View view) {
		super(page);
		setText("Restore view");
	}

	@Override
	protected Command getCommand() {
		TransactionalEditingDomain domain = getDiagramEditPart()
				.getEditingDomain();
		List<EditPart> selectedEditParts = MDTUtil
				.getEditPartsFromSelection(getSelection());
		if (selectedEditParts == null || selectedEditParts.isEmpty()) {
			return null;
		}
		IGraphicalEditPart graphicalEditPartToUse = null;
		if (selectedEditParts.get(0) instanceof IGraphicalEditPart) {
			graphicalEditPartToUse = ((IGraphicalEditPart) selectedEditParts
					.get(0));
		}
		if (graphicalEditPartToUse == null) {
			return null;
		}
		final IGraphicalEditPart finalEditPartToUse = graphicalEditPartToUse;
		RestoreViewCommand restoreCommand = new RestoreViewCommand(
				finalEditPartToUse, domain, "restore view");
		UpdateDiagramCommand updateCommand = new UpdateDiagramCommand(
				finalEditPartToUse);
		CompoundCommand cc = new CompoundCommand("Restoer and refresh");
		if (restoreCommand != null && restoreCommand.canExecute()) {
			cc.add(restoreCommand.toGEFCommand());
		}
		if (updateCommand != null && updateCommand.canExecute()) {
			cc.add(updateCommand);
		}
		if (cc.canExecute()) {
			return cc;
		}
		return null;
	}

	@Override
	protected Request createTargetRequest() {
		// no request is needed by this action
		return null;
	}

	@Override
	protected boolean isSelectionListener() {
		return true;
	}

}