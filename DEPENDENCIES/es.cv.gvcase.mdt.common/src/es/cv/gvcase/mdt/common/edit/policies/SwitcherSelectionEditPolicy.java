/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.edit.parts.IItemGroup;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;

/**
 * {@link EditPolicy} to be installed under the
 * {@link EditPolicy#SELECTION_FEEDBACK_ROLE}. <br>
 * This policy changes figures' elements according to a switcher that directs
 * the views and elements to show on each selection. <br>
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class SwitcherSelectionEditPolicy extends SelectionEditPolicy {

	IItemGroup itemGroup = null;

	/**
	 * Constructor. An {@link IItemGroup} is mandatory.
	 * 
	 * @param switcher
	 */
	public SwitcherSelectionEditPolicy(IItemGroup switcher) {
		super();
		if (switcher == null) {
			throw new IllegalArgumentException("Switcher must not be null");
		}
		this.itemGroup = switcher;
	}

	@Override
	protected void hideSelection() {
		// nothing to do
	}

	/**
	 * Executes a command that changes the element of a View so that the View
	 * shows the correct figure and info.
	 * 
	 */
	@Override
	protected void showSelection() {
		EObject eObject = itemGroup.getSelected();
		TransactionalEditingDomain domain = TransactionUtil
				.getEditingDomain(eObject);
		new UpdateViewElementCommand(itemGroup, domain).executeInTransaction();
	}

	/**
	 * Command to update/change the view of an element.
	 * 
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 * 
	 */
	protected class UpdateViewElementCommand extends
			AbstractCommonTransactionalCommmand {

		protected IItemGroup switcher = null;

		public UpdateViewElementCommand(IItemGroup switcher,
				TransactionalEditingDomain domain) {
			super(domain, "Show selected edit part", null);
			this.switcher = switcher;
		}

		@Override
		protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
				IAdaptable info) throws ExecutionException {

			EObject eObject = switcher.getEObjectForView();
			View viewToShow = switcher.getTargetView();
			if (viewToShow != null) {
				viewToShow.setElement(eObject);
				DiagramEditPartsUtil
						.updateEditPart((IGraphicalEditPart) getHost());
			}

			return CommandResult.newOKCommandResult();
		}
	}

}
