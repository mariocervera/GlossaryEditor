/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;

/**
 * Extends a {@link ViewAndFeatureResolver} to provide additional commands when
 * a DragAndDrop is performes.
 * 
 * @author fjcano
 * 
 */
public interface DragAndDropResolver extends ViewAndFeatureResolver {

	/**
	 * Provides a hook point that the dragAndDropEditPolicy will call when
	 * creating the commands to drop an element in an IGraphicalEditPart.
	 * 
	 * @param eobject
	 * @param targetEditpart
	 * @return
	 */
	AbstractCommonTransactionalCommmand getDroppingElementResolverCommand(
			EObject eObject, IGraphicalEditPart targetEditpart);

}
