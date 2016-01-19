/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
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
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * The Class AddEObjectReferencesToDiagram.
 */
public class AddEObjectReferencesToDiagram extends
		AbstractCommonTransactionalCommmand {

	/** The diagram. */
	private Diagram diagram = null;

	/** The e objects. */
	private List<EObject> eObjects = null;

	/**
	 * Flag to add the {@link EObject}'s contents.
	 */
	protected boolean addContents = true;

	/**
	 * Instantiates a new adds the {@link EObject} references to {@link Diagram}.
	 * 
	 * @param domain
	 *            the domain
	 * @param diagram
	 *            the diagram
	 * @param eObjects
	 *            the e objects
	 */
	public AddEObjectReferencesToDiagram(TransactionalEditingDomain domain,
			Diagram diagram, List<EObject> eObjects) {
		super(domain, "Add EObject references to Diagram", null);
		this.diagram = diagram;
		this.eObjects = eObjects;
	}

	public AddEObjectReferencesToDiagram(TransactionalEditingDomain domain,
			Diagram diagram, List<EObject> eObjects, boolean addContents) {
		super(domain, "Add EObject references to Diagram", null);
		this.diagram = diagram;
		this.eObjects = eObjects;
		this.addContents = addContents;
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
		for (EObject eObject : eObjects) {
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
		if (addContents) {
			for (EObject e : eObject.eContents()) {
				if (e instanceof EAnnotation) {
					continue;
				}
				addReferences(e);
			}
		}
		MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram, eObject);
	}

}
