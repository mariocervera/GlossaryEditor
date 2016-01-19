/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Mario Cervera Ubeda (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions.registry;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;

public interface ICommandProvider {
	
	public static final String CREATE_ACTION = "Create";
	
	public static final String DELETE_ACTION = "Delete";

	public Command getCommand(EObject eobj, String action);
}
