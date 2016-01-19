/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import es.cv.gvcase.mdt.common.diagram.editparts.AbstractBorderedShapeEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.BorderedBorderItemEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.CompartmentEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ConnectionNodeEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.DiagramEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.LabelEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ListCompartmentEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ShapeCompartmentEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ShapeNodeEditPart;
import es.cv.gvcase.mdt.common.util.MDTUtil;

public class DeleteFromDiagramPopupMenuContributionPolicy implements
		IPopupMenuContributionPolicy {

	public boolean appliesTo(ISelection selection,
			IConfigurationElement configuration) {
		// all the selected edit parts should be of our types
		for (Object o : ((StructuredSelection) selection).toList()) {
			if (!(o instanceof AbstractBorderedShapeEditPart)
					&& !(o instanceof CompartmentEditPart)
					&& !(o instanceof ConnectionNodeEditPart)
					&& !(o instanceof DiagramEditPart)
					&& !(o instanceof LabelEditPart)
					&& !(o instanceof ListCompartmentEditPart)
					&& !(o instanceof ShapeCompartmentEditPart)
					&& !(o instanceof ShapeNodeEditPart)
					&& !(o instanceof BorderedBorderItemEditPart)) {
				return false;
			}
		}

		List<EObject> eObjects = MDTUtil.getEObjectsFromSelection(selection);
		if (eObjects != null && eObjects.size() > 0) {
			return true;
		}
		return false;
	}

}
