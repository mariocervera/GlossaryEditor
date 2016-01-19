/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
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

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.internal.editpolicies.ConnectionEditPolicy;

/**
 * A Connection edit policy that removes the references from the diagram when
 * the connection is deleted.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class DeleteReferencedConnectionEditPolicy extends ConnectionEditPolicy {
	@Override
	protected boolean shouldDeleteSemantic() {
		return false;
	}

	@Override
	protected org.eclipse.gef.commands.Command createDeleteViewCommand(
			org.eclipse.gef.requests.GroupRequest deleteRequest) {
		org.eclipse.gef.commands.Command command = super
				.createDeleteViewCommand(deleteRequest);
		// this command removes all the references of the host edit part fro the
		// diagram
		command = command
				.chain(new org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy(
						new es.cv.gvcase.mdt.common.commands.RemoveEObjectReferencesFromDiagram(
								getEditingDomain(), getHostEditPart()
										.getNotationView().getDiagram(),
								Collections.singletonList(getHostEditPart()
										.resolveSemanticElement()))));
		return command;
	}

	protected TransactionalEditingDomain getEditingDomain() {
		if (getHostEditPart() != null) {
			return getHostEditPart().getEditingDomain();
		}
		return null;
	}

	protected IGraphicalEditPart getHostEditPart() {
		if (getHost() instanceof IGraphicalEditPart) {
			return (IGraphicalEditPart) getHost();
		}
		return null;
	}
}
