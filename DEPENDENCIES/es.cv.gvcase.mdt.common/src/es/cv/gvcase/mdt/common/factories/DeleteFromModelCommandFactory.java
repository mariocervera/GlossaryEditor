/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.factories;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.IStructuredSelection;

import es.cv.gvcase.mdt.common.util.MDTUtil;

public abstract class DeleteFromModelCommandFactory {
	/**
	 * gets the selected EObjects from the selection
	 * 
	 * @return
	 */
	public List<EObject> getSelectedEObjects(IStructuredSelection selection) {
		return MDTUtil.getEObjectsFromSelection(selection);
	}

	/**
	 * gets the selected EditParts from the selection
	 * 
	 * @return
	 */
	public List<EditPart> getSelectedEditParts(IStructuredSelection selection) {
		return MDTUtil.getEditPartsFromSelection(selection);
	}

	/**
	 * Checks if this factory can provides a Command for the selected objects
	 * 
	 * @param selection
	 * @return
	 */
	public abstract boolean provides(IStructuredSelection selection);

	/**
	 * function for create new specified commands
	 * 
	 * @return
	 */
	public abstract Command createNewDeleteFromModelCommand(
			IStructuredSelection selection);
}
