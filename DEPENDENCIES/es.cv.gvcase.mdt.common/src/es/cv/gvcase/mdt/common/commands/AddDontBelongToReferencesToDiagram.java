/***************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte,
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
 * Command to add a list of elements to the list of references in the annotation
 * that is used to filter using a blacklist policy.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class AddDontBelongToReferencesToDiagram extends
		AbstractCommonTransactionalCommmand {

	/**
	 * Diagram to manipulate its EAnnotation.
	 */
	protected Diagram diagram = null;

	public Diagram getDiagram() {
		return diagram;
	}

	/**
	 * List of EObjects to add.
	 */
	protected List<EObject> eObjects = null;

	public List<EObject> getEObjects() {
		return eObjects;
	}

	protected boolean addContents = true;

	public boolean isAddContents() {
		return addContents;
	}

	/**
	 * Constructor.
	 * 
	 * @param domain
	 * @param diagram
	 * @param eObjects
	 */
	public AddDontBelongToReferencesToDiagram(
			TransactionalEditingDomain domain, Diagram diagram,
			List<EObject> eObjects) {
		this(domain, diagram, eObjects, true);
	}

	/**
	 * Constructor.
	 * 
	 * @param domain
	 * @param diagram
	 * @param eObjects
	 * @param addContents
	 */
	public AddDontBelongToReferencesToDiagram(
			TransactionalEditingDomain domain, Diagram diagram,
			List<EObject> eObjects, boolean addContents) {
		super(domain, "Add 'dont belong to' EObject references to Diagram",
				null);
		this.diagram = diagram;
		this.eObjects = eObjects;
		this.addContents = addContents;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		for (EObject eObject : getEObjects()) {
			addReferences(eObject);
		}
		return CommandResult.newOKCommandResult();
	}

	/**
	 * Adds the references.
	 * 
	 * @param eObject
	 *            the e object
	 */
	private void addReferences(EObject eObject) {
		if (isAddContents()) {
			for (EObject e : eObject.eContents()) {
				addReferences(e);
			}
		}
		Diagram diagram = getDiagram();
		MultiDiagramUtil.AddDoesNotBelongEAnnotationReferenceToDiagram(diagram,
				eObject);
	}

}
