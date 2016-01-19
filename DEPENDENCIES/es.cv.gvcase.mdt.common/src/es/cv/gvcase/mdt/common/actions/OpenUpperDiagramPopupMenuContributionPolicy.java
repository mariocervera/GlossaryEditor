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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.mdt.common.part.CachedResourcesDiagramEditor;
import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class OpenUpperDiagramPopupMenuContributionPolicy.
 */
public class OpenUpperDiagramPopupMenuContributionPolicy implements
		IPopupMenuContributionPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.common.ui.services.action.contributionitem.
	 * IPopupMenuContributionPolicy
	 * #appliesTo(org.eclipse.jface.viewers.ISelection,
	 * org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean appliesTo(ISelection selection,
			IConfigurationElement configuration) {
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor instanceof CachedResourcesDiagramEditor == false
				&& activeEditor instanceof MOSKittMultiPageEditor == false) {
			return false;
		}

		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			if (ss.size() <= 0 || ss.size() > 1) {
				return false;
			}
			Object object = ss.getFirstElement();
			if (object instanceof IGraphicalEditPart) {
				if (((IGraphicalEditPart) object).getNotationView() instanceof Diagram) {
					Diagram diagram = (Diagram) ((IGraphicalEditPart) object)
							.getNotationView();
					if (MultiDiagramUtil.getUpperDiagram(diagram) != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
