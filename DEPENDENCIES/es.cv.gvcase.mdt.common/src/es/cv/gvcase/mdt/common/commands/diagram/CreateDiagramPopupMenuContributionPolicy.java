/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.commands.diagram;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

import es.cv.gvcase.mdt.common.part.CachedResourcesDiagramEditor;
import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Filter for "Create diagram" context menu action. <br>
 * Will make the action available if the selected editPart has any diagram kinds
 * that can be created.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class CreateDiagramPopupMenuContributionPolicy implements
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
		IEditorPart editorPart = MDTUtil.getActiveEditor();
		if (editorPart instanceof CachedResourcesDiagramEditor == false
				&& editorPart instanceof MOSKittMultiPageEditor == false) {
			return false;
		}

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = ((IStructuredSelection) selection);
			if (ss.size() <= 0 || ss.size() > 1) {
				return false;
			}
			Object first = ss.getFirstElement();
			if (first instanceof IGraphicalEditPart) {
				return !EClassToDiagramRegistry.getInstance()
						.getDiagramsForEditPart((IGraphicalEditPart) first)
						.isEmpty();
			}
		}
		return false;
	}

}
