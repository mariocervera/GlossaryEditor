/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.diagram.editparts;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.util.FigureUtils;

/**
 * This class is created to be used in the necessary EditParts to remove the
 * "Delete From Model" and "Delete From Diagram" actions from GMF when reading
 * the provider
 * org.eclipse.gmf.runtime.diagram.ui.providers.DiagramContextMenuProvider to
 * add or remove this actions. Also, is used to add the MOSKitt
 * "Delete From Model" and "Delete From Diagram" when reading the same provider.
 * 
 * @author <a href="mailto:mgil@prodevelop.es">Marc Gil</a>
 */
public class DiagramEditPart extends
		org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart implements
		MOSKittGraphicalEditPart {

	public DiagramEditPart(View diagramView) {
		super(diagramView);
	}

	@Override
	protected void handleNotificationEvent(Notification notification) {
		super.handleNotificationEvent(notification);
		// notify all SemanticFigures of the changes.
		FigureUtils.notifySemanticFigures(this, notification);
	}

	public boolean acceptDiagramDecoration() {
		return true;
	}
}
