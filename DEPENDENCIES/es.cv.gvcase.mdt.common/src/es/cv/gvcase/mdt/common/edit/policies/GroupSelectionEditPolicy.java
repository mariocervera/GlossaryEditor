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

import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.actions.internal.GroupAction;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;

import es.cv.gvcase.mdt.common.edit.parts.group.IViewGroup;
import es.cv.gvcase.mdt.common.edit.parts.group.ViewGroupRegistry;

/**
 * A {@link SelectionEditPolicy} specialized to work with {@link IViewGroup}s. <br>
 * This policy controls the selection in the editor so that when an element of a
 * group is selected, the whole group is selected.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see IViewGroup
 * @see GroupAction
 * 
 */
public class GroupSelectionEditPolicy extends SelectionEditPolicy {

	/**
	 * {@link GroupSelectionEditPolicy}'s role identifier.
	 */
	public static final String GROUP_SELECTION_FEEDBACK_ROLE = "es.cv.gvcase.mdt.common.feedback";

	@Override
	protected void hideSelection() {
		// nothing to do
	}

	/**
	 * Upon selection on an element, checks whether the element belongs to a
	 * group and if so, sets the editor selection to hold all the elements in
	 * the group.
	 */
	@Override
	protected void showSelection() {
		// find whether the host edit part is in a group
		IGraphicalEditPart host = getHostGraphicalEditPart();
		if (ViewGroupRegistry.getInstance().isInGroup(host)) {
			IViewGroup group = ViewGroupRegistry.getInstance().getViewGroupFor(
					host);
			if (group != null) {
				// select all edit parts of the group
				group.selectGroupEditPartsSelection();
			}
		}
	}

	/**
	 * Gets the host edit part as an {@link IGraphicalEditPart}.
	 * 
	 * @return
	 */
	protected IGraphicalEditPart getHostGraphicalEditPart() {
		return getHost() instanceof IGraphicalEditPart ? (IGraphicalEditPart) getHost()
				: null;
	}

}
