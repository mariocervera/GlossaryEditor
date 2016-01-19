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
 * The Class RemoveEObjectReferencesFromDiagram.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class RemoveEObjectReferencesFromDiagram extends
		AbstractCommonTransactionalCommmand {

	/** The {@link Diagram} to remove the references from. */
	private Diagram diagram = null;

	/** The {@link EObject}s to remove from the reference list. */
	private List<EObject> eObjects = null;

	/**
	 * Instantiates a new {@link RemoveEObjectReferencesFromDiagram}.
	 * 
	 * @param domain
	 *            the domain
	 * @param diagram
	 *            the diagram
	 * @param eObjects
	 *            the e objects
	 */
	public RemoveEObjectReferencesFromDiagram(
			TransactionalEditingDomain domain, Diagram diagram,
			List<EObject> eObjects) {
		super(domain, "Add EObject references to Diagram", null);
		this.diagram = diagram;
		this.eObjects = eObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.AbstractOperation#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return diagram != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.emf.commands.core.command.
	 * AbstractTransactionalCommand
	 * #doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 * org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		// remove all references, one by one, from the Diagram.
		for (EObject eObject : eObjects) {
			MultiDiagramUtil.RemoveEAnnotationReferenceFromDiagram(diagram,
					eObject);
		}
		// all went well.
		return CommandResult.newOKCommandResult();
	}
}
