/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;

import es.cv.gvcase.mdt.common.commands.OpenAsDiagramCommand;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * 
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class CreateDiagramAction extends SelectionListenerAction {

	public CreateDiagramAction() {
		super("Create diagram");
	}

	/**
	 * Gets the diagram.
	 * 
	 * @return the diagram
	 */
	protected EObject getElement() {
		IStructuredSelection selection = getStructuredSelection();
		Object selected = selection.getFirstElement();
		if (selected instanceof EObject) {
			return ((EObject) selected);
		}
		return null;
	}

	@Override
	public void run() {
		// as of current version super.run() does nothing.
		super.run();
		/*
		 * 1) retrieve selected element. 2) see if this element can have
		 * diagrams created on it. 3) open create elements dialog and create the
		 * diagram.
		 */
		EObject element = getElement();
		if (element == null) {
			return;
		} else {
			Class clazz = element.getClass();
			String diagramKindToCreate = MDTUtil.getDiagramKindToCreate(clazz);
			TransactionalEditingDomain domain = TransactionUtil
					.getEditingDomain(element);
			GMFResource diagramResource = MultiDiagramUtil.getDiagramResource();
			if (diagramResource != null && diagramKindToCreate != null
					&& domain != null) {
				OpenAsDiagramCommand command = new OpenAsDiagramCommand(diagramResource, element, diagramKindToCreate);
				command.executeInTransaction();
			}
		}
	}

}

