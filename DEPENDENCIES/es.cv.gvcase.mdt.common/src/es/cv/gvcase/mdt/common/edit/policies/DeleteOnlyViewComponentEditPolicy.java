/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ComponentEditPolicy;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.commands.AddDontBelongToReferencesToDiagram;
import es.cv.gvcase.mdt.common.commands.RemoveEObjectReferencesFromDiagram;

/**
 * A ComponentEditPolicy that prevents the deletion of the underlying models.
 * Will delete the view and remove the element's reference from the diagram upon
 * a delete request.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DeleteOnlyViewComponentEditPolicy extends ComponentEditPolicy {

	/**
	 * Gets the graphical host.
	 * 
	 * @return the graphical host
	 */
	protected IGraphicalEditPart getGraphicalHost() {
		if (getHost() instanceof IGraphicalEditPart) {
			return (IGraphicalEditPart) getHost();
		}
		return null;
	}

	/**
	 * Won't delete the underlying model element.
	 * 
	 * @return true, if should delete semantic
	 */
	@Override
	protected boolean shouldDeleteSemantic() {
		return false;
	}

	/**
	 * Will delete the view and remove the EObject's reference from the
	 * diagram's list of elements to show.
	 * 
	 * @param request
	 *            the request
	 * 
	 * @return the delete command
	 */
	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		IGraphicalEditPart editPart = getGraphicalHost();
		TransactionalEditingDomain domain = editPart != null ? editPart
				.getEditingDomain() : null;
		View view = editPart != null ? editPart.getNotationView() : null;
		Diagram diagram = view != null ? view.getDiagram() : null;
		EObject element = editPart != null ? editPart.resolveSemanticElement()
				: null;
		Command command = super.getDeleteCommand(request);
		if (domain != null && diagram != null && element != null) {
			AbstractCommonTransactionalCommmand transactionalCommand = null;
			// the command to be executed is different according to the type of
			// policy (whitelist | blacklist) installed in the edit part.
			if (ShowInDiagramEditPartPolicyRegistry.getInstance()
					.isEditPartWhiteListPolicy(editPart.getParent())) {
				// for a whitelist, the element has to be removed from the list
				// of allowed elements.
				transactionalCommand = new RemoveEObjectReferencesFromDiagram(
						domain, diagram, Collections.singletonList(element));
			} else {
				// for a blacklist, the element has to be added to the list of
				// forbidden elements.
				transactionalCommand = new AddDontBelongToReferencesToDiagram(
						domain, diagram, Collections.singletonList(element));
			}
			command = command.chain(transactionalCommand.toGEFCommand());
		}
		return command;
	}
}
