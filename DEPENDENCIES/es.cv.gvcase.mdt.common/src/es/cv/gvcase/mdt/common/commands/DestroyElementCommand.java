/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;

/**
 * This class override the doExecuteWithResult method to delete (not remove) the
 * containing elements recursively
 * 
 * @author mgil
 */
public class DestroyElementCommand extends
		org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand {

	public DestroyElementCommand(DestroyElementRequest request) {
		super(request);
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		EObject destructee = getElementToDestroy();

		// only destroy attached elements
		if ((destructee != null) && (destructee.eResource() != null)) {
			// tear down incoming references
			tearDownIncomingReferences(destructee);

			// also tear down outgoing references, because we don't want
			// reverse-reference lookups to find destroyed objects
			tearDownOutgoingReferences(destructee);

			// remove the object from its container
			EcoreUtil.delete(destructee, true);

			// in case it was cross-resource-contained
			Resource res = destructee.eResource();
			if (res != null) {
				res.getContents().remove(destructee);
			}
		}

		return CommandResult.newOKCommandResult();
	}

}
