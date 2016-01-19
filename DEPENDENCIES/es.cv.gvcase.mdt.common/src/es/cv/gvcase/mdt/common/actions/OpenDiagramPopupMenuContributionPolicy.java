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
package es.cv.gvcase.mdt.common.actions;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * The Class OpenAsDiagramPopupMenuContributionPolicy.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenDiagramPopupMenuContributionPolicy implements
		IPopupMenuContributionPolicy {

	public boolean appliesTo(ISelection selection,
			IConfigurationElement configuration) {

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = ((IStructuredSelection) selection);
			if (ss.size() <= 0 || ss.size() > 1) {
				return false;
			}
			Object first = ss.getFirstElement();
			if (first instanceof IGraphicalEditPart) {
				View view = ((IGraphicalEditPart) first).getNotationView();
				if (view instanceof Diagram) {
					return false;
				}
				if (MultiDiagramUtil.getDiagramsAssociatedToElement(
						view.getElement()).size() > 0) {
					return true;
				}
			}
		}

		return false;
	}

}
