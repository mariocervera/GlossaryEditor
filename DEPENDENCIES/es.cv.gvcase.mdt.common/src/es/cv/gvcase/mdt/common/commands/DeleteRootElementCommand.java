/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

public class DeleteRootElementCommand extends
		org.eclipse.emf.edit.command.DeleteCommand {
	private EObject elementToDelete;
	private Resource resource;

	public DeleteRootElementCommand(TransactionalEditingDomain domain,
			EObject element, List affectedFiles) {
		super(domain, affectedFiles);
		elementToDelete = element;
		resource = elementToDelete.eResource();
	}

	@Override
	public void execute() {
		if (elementToDelete == null) {
			return;
		}

		redo();
	}

	@Override
	public boolean canExecute() {
		return elementToDelete != null && resource != null
				&& DeleteRootElementCommand.isRootElement(elementToDelete) == true;
	}

	@Override
	public void redo() {
		resource.getContents().remove(elementToDelete);
	}

	@Override
	public void undo() {
		resource.getContents().add(elementToDelete);
	}

	/**
	 * Check if the given eObject is a root element
	 * 
	 * @param eObject
	 * @return
	 */
	public static boolean isRootElement(EObject eObject) {
		return eObject != null && eObject.eContainer() == null;
	}
}
