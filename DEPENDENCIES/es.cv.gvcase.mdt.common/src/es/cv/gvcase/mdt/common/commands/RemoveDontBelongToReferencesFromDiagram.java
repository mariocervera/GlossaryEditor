/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * Command to remove a list of elements to the list of references in the annotation
 * that is used to filter using a blacklist policy.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class RemoveDontBelongToReferencesFromDiagram extends
		AbstractCommonTransactionalCommmand {

	/**
	 * Diagram to manpulate its EAnnotation.
	 */
	protected Diagram diagram = null;

	public Diagram getDiagram() {
		return diagram;
	}

	/**
	 * List of EObjects to remove from the list.
	 */
	protected List<EObject> eObjects = null;

	public List<EObject> getEObjects() {
		return eObjects;
	}

	/**
	 * Constructor.
	 * 
	 * @param domain
	 * @param diagram
	 * @param eObjects
	 */
	public RemoveDontBelongToReferencesFromDiagram(
			TransactionalEditingDomain domain, Diagram diagram,
			List<EObject> eObjects) {
		super(domain, "Remove 'dont belong to' references from diagram", null);
		this.diagram = diagram;
		this.eObjects = eObjects;
	}

	@Override
	public boolean canExecute() {
		return getDiagram() != null;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		// remove all references, one by one, from the Diagram.
		Diagram diagram = getDiagram();
		for (EObject eObject : getEObjects()) {
			MultiDiagramUtil
					.RemoveDoesNotBelongEAnnotationReferenceFromDiagram(
							diagram, eObject);
		}
		// all went well.
		return CommandResult.newOKCommandResult();
	}

}
